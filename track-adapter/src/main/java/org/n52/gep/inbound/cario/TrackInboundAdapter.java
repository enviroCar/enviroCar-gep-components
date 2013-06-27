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
package org.n52.gep.inbound.cario;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.n52.gep.commons.inbound.AbstractInboundAdapter;
import org.n52.gep.commons.json.JsonUtil;
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
	
	private static final String[] PHENOMENON_FIELDS = new String [] {
		TrackInboundDefinition.CONSUMPTION_KEY,
		TrackInboundDefinition.CO2_KEY,
		TrackInboundDefinition.SPEED_KEY,
		TrackInboundDefinition.MAF_KEY
	};


	public TrackInboundAdapter(AdapterDefinition definition)
			throws ComponentException {
		super(definition);
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

	private void appendFeaturesToQueue(Map<?, ?> json) {
		if (!json.containsKey(FEATURES_KEY)
				|| !json.containsKey(PROPERTIES_KEY)
				|| !json.containsKey(TrackInboundDefinition.TYPE_KEY))
			return;

		List<?> features = (List<?>) json.get(FEATURES_KEY);
		Map<?, ?> props = (Map<?, ?>) json.get(PROPERTIES_KEY);
		String type = (String) json.get(TrackInboundDefinition.TYPE_KEY);
		if (!type.equals("FeatureCollection"))
			return;

		String sensor;
		if (props.containsKey(TrackInboundDefinition.SENSOR_KEY)) {
			sensor = (String) props.get(TrackInboundDefinition.SENSOR_KEY);
		} else {
			sensor = "null";
		}

		for (Object feature : features) {
			if (feature instanceof Map<?, ?>) {
				Map<?, ?> mapFeature = (Map<?, ?>) feature;
				try {
					GeoEvent geoEvent = prepareGeoEvent(sensor);
					fillGeoEvent(geoEvent, mapFeature);
					this.eventQueue.add(geoEvent);
				} catch (MessagingException e) {
					logger.warn(e.getMessage(), e);
				} catch (FieldException e) {
					logger.warn(e.getMessage(), e);
				}
			}
		}

	}

	private void fillGeoEvent(GeoEvent geoEvent, Map<?, ?> mapFeature) throws FieldException {
		String type = (String) mapFeature.get(TrackInboundDefinition.TYPE_KEY);
		geoEvent.setField(TrackInboundDefinition.TYPE_KEY, type);
		
		Geometry geometry = createGeometry((Map<?, ?>) mapFeature.get(TrackInboundDefinition.GEOMETRY_KEY));
		geoEvent.setField(TrackInboundDefinition.GEOMETRY_KEY, geometry);

		Map<?, ?> props = (Map<?, ?>) mapFeature.get(PROPERTIES_KEY);
		Date time = null;
		try {
			time = isoDateTimeFormat.parse((String) props.get(TrackInboundDefinition.TIME_KEY));
		} catch (ParseException e) {
			logger.warn(e.getMessage());
		}
		geoEvent.setField(TrackInboundDefinition.TIME_KEY, time == null ? new Date() : time);
		
		Map<?, ?> phens = (Map<?, ?>) props.get(PHENOMENONS_KEY);
		
		for (String key : PHENOMENON_FIELDS) {
			geoEvent.setField(key, ((Map<?, ?>) phens.get(key)).get(VALUE_KEY));
		}
	}

	private Geometry createGeometry(Map<?, ?> object) {
		String type = (String) object.get("type");
		if (type.equals("Point")) {
			return PointImpl.fromList((List<?>) object.get("coordinates"));
		}
		return null;
	}

	private GeoEvent prepareGeoEvent(String sensor) throws MessagingException,
			FieldException {
		GeoEventDefinition def = ((AdapterDefinition) definition)
				.getGeoEventDefinition(TrackInboundDefinition.DEFINITION_NAME);
		GeoEvent result = geoEventCreator.create(def.getGuid());
		result.setField(TrackInboundDefinition.SENSOR_KEY, sensor);
		return result;
	}

	@Override
	protected synchronized GeoEvent createNextRemainingGeoEvent() {
		return this.eventQueue.poll();
	}

	@Override
	protected synchronized boolean allDataConsumed() {
		return this.eventQueue.isEmpty();
	}

}
