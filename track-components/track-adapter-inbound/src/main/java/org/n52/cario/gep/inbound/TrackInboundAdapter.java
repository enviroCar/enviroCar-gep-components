/**
 * Copyright (C) 2004
 * by 52 North Initiative for Geospatial Open Source Software GmbH
 *
 * Contact: Andreas Wytzisk
 * 52 North Initiative for Geospatial Open Source Software GmbH
 * Martin-Luther-King-Weg 24
 * 48155 Muenster, Germany
 * info@52north.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.n52.cario.gep.inbound;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.n52.cario.gep.commons.definitions.TrackDefinition;
import org.n52.cario.gep.commons.inbound.AbstractInboundAdapter;
import org.n52.cario.gep.commons.json.JsonUtil;
import org.n52.cario.parser.sensor.SensorParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.esri.ges.adapter.AdapterDefinition;
import com.esri.ges.core.component.ComponentException;
import com.esri.ges.core.geoevent.FieldException;
import com.esri.ges.core.geoevent.GeoEvent;
import com.esri.ges.core.geoevent.GeoEventDefinition;
import com.esri.ges.messaging.MessagingException;
import com.esri.ges.spatial.Geometry;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

public class TrackInboundAdapter extends AbstractInboundAdapter {

	private Queue<GeoEvent> eventQueue = new ConcurrentLinkedQueue<GeoEvent>();

	private static DateFormat isoDateTimeFormat = new ISO8601DateFormat();

	private static final Logger logger = LoggerFactory
			.getLogger(TrackInboundAdapter.class);

	private static final String FEATURES_KEY = "features";
	private static final String PROPERTIES_KEY = "properties";
	private static final String PHENOMENONS_KEY = "phenomenons";
	private static final String VALUE_KEY = "value";
	private static final String GEOMETRY_KEY = "geometry";
	private static final String TIME_KEY = "time";
	private static final String ID_KEY = "id";
	private static final String TYPE_KEY = "type";
	
	private static final String[] PHENOMENON_FIELDS = new String [] {
		"Consumption",
		"CO2",
		"Speed",
		"MAF"
	};


	public TrackInboundAdapter(AdapterDefinition definition)
			throws ComponentException {
		super(definition);
	}

	@Override
	protected synchronized boolean allDataConsumed() {
		return this.eventQueue.isEmpty();
	}

	@Override
	protected synchronized GeoEvent createGeoEvent(InputStream input) {
		try {
			Map<?, ?> json = JsonUtil.createJson(input);
			appendFeaturesToQueue(json);
			return this.eventQueue.poll();
		} catch (RuntimeException e) {
			logger.warn(e.getMessage(), e);
			return null;
		} catch (IOException e) {
			logger.warn(e.getMessage(), e);
			return null;
		}
	}

	@Override
	protected synchronized GeoEvent createNextRemainingGeoEvent() {
		return this.eventQueue.poll();
	}

	private void appendFeaturesToQueue(Map<?, ?> json) {
		if (!json.containsKey(FEATURES_KEY)
				|| !json.containsKey(PROPERTIES_KEY)
				|| !json.containsKey(TrackDefinition.TYPE_KEY))
			return;
		
		String type = (String) json.get(TrackDefinition.TYPE_KEY);
		if (!type.equals("FeatureCollection"))
			return;

		List<?> features = (List<?>) json.get(FEATURES_KEY);
		Map<?, ?> props = (Map<?, ?>) json.get(PROPERTIES_KEY);
		
		String sensor = parseSensor(props);
		
		String id = parseId(props);

		for (Object feature : features) {
			if (feature instanceof Map<?, ?>) {
				Map<?, ?> mapFeature = (Map<?, ?>) feature;
				try {
					GeoEvent geoEvent = prepareGeoEvent(sensor, id);
					fillGeoEvent(geoEvent, mapFeature);
					logger.info("Adding {} to eventQueue tail.", geoEvent);
					this.eventQueue.add(geoEvent);
				} catch (MessagingException e) {
					logger.warn(e.getMessage(), e);
				} catch (FieldException e) {
					logger.warn(e.getMessage(), e);
				}
			}
		}

	}

	private Geometry createGeometry(Map<?, ?> object) {
		String type = (String) object.get(TYPE_KEY);
		if (type.equals("Point")) {
			return PointImpl.fromList((List<?>) object.get("coordinates"));
		}
		return null;
	}

	private void fillGeoEvent(GeoEvent geoEvent, Map<?, ?> mapFeature) throws FieldException {
		Geometry geometry = createGeometry((Map<?, ?>) mapFeature.get(GEOMETRY_KEY));
		geoEvent.setField(TrackDefinition.GEOMETRY_KEY, geometry);

		Map<?, ?> props = (Map<?, ?>) mapFeature.get(PROPERTIES_KEY);
		Date time = parseDateTime(props);
		geoEvent.setField(TrackDefinition.TIME_KEY, time == null ? new Date() : time);
		
		parsePhenomena(geoEvent, props);
	}

	private Date parseDateTime(Map<?, ?> props) {
		Date time = null;
		try {
			time = isoDateTimeFormat.parse((String) props.get(TIME_KEY));
		} catch (ParseException e) {
			logger.warn(e.getMessage());
		}
		return time;
	}

	private String parseId(Map<?, ?> props) {
		String id;
		if (props.containsKey(ID_KEY)) {
			id = (String) props.get(ID_KEY);
		} else {
			id = "null";
		}
		return id;
	}

	private void parsePhenomena(GeoEvent geoEvent, Map<?, ?> props)
			throws FieldException {
		Map<?, ?> phens = (Map<?, ?>) props.get(PHENOMENONS_KEY);
		
		for (String key : PHENOMENON_FIELDS) {
			geoEvent.setField(key.toLowerCase(), ((Map<?, ?>) phens.get(key)).get(VALUE_KEY));
		}
	}

	private String parseSensor(Map<?, ?> props) {
		String sensor;
		if (props.containsKey(TrackDefinition.SENSOR_KEY)) {
			SensorParser sensorParser = new SensorParser();
			sensorParser.parse(props.get(TrackDefinition.SENSOR_KEY));
			sensor = sensorParser.getId();
		} else {
			sensor = "null";
		}
		return sensor;
	}

	private GeoEvent prepareGeoEvent(String sensor, String id) throws MessagingException,
			FieldException {
		GeoEventDefinition def = ((AdapterDefinition) definition)
				.getGeoEventDefinition(TrackDefinition.DEFINITION_NAME);
		GeoEvent result = geoEventCreator.create(def.getGuid());
		result.setField(TrackDefinition.SENSOR_KEY, sensor);
		result.setField(TrackDefinition.ID_KEY, id);
		return result;
	}

}
