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
package org.n52.cario.gep.commons.definitions;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.esri.ges.adapter.AdapterDefinitionBase;
import com.esri.ges.adapter.AdapterType;
import com.esri.ges.core.ConfigurationException;
import com.esri.ges.core.geoevent.DefaultFieldDefinition;
import com.esri.ges.core.geoevent.DefaultGeoEventDefinition;
import com.esri.ges.core.geoevent.FieldDefinition;
import com.esri.ges.core.geoevent.FieldType;
import com.esri.ges.core.geoevent.GeoEventDefinition;

public class TrackDefinition extends AdapterDefinitionBase {

	public static final String DEFINITION_NAME = "enviroCar-track-definition";
	
	private static final Logger logger = LoggerFactory
			.getLogger(TrackDefinition.class);

	public static final String TYPE_KEY = "type";
	public static final String SENSOR_KEY = "sensor";
	public static final String GEOMETRY_KEY = "shape";
	public static final String TIME_KEY = "starttime";
	public static final String CONSUMPTION_KEY = "consumption";
	public static final String CO2_KEY = "co2";
	public static final String SPEED_KEY = "speed";
	public static final String ID_KEY = "trackid";
	public static final String ENVIROCAR_TRACK_KEY = "enviroCar-track";
	public static final String BEARING_KEY = "bearing";
	
	private static final String PRECONFIGURED_DEFINITION = "enviroCarTrack";

	public TrackDefinition() {
		super(AdapterType.INBOUND);
		try {
			if (geoEventDefinitions.get(PRECONFIGURED_DEFINITION) != null) {
				geoEventDefinitions.put(DEFINITION_NAME, geoEventDefinitions.get(PRECONFIGURED_DEFINITION));
			}
			else {
				GeoEventDefinition md = new DefaultGeoEventDefinition();
				md.setName(DEFINITION_NAME);
				List<FieldDefinition> fieldDefinitions = new ArrayList<FieldDefinition>();

				//"TRACK_ID" is the resolved unique identifier for the feature 
				fieldDefinitions.add(new DefaultFieldDefinition(ID_KEY,
						FieldType.String, "TRACK_ID"));
				fieldDefinitions.add(new DefaultFieldDefinition(SENSOR_KEY,
						FieldType.String));
				fieldDefinitions.add(new DefaultFieldDefinition(GEOMETRY_KEY,
						FieldType.Geometry, "GEOMETRY"));
				fieldDefinitions.add(new DefaultFieldDefinition(TIME_KEY,
						FieldType.Date, "TIME_START"));
				fieldDefinitions.add(new DefaultFieldDefinition(CONSUMPTION_KEY,
						FieldType.Double));
				fieldDefinitions.add(new DefaultFieldDefinition(CO2_KEY,
						FieldType.Double));
				fieldDefinitions.add(new DefaultFieldDefinition(SPEED_KEY,
						FieldType.Double));
				fieldDefinitions.add(new DefaultFieldDefinition(BEARING_KEY,
						FieldType.Double));
				fieldDefinitions.add(new DefaultFieldDefinition(ENVIROCAR_TRACK_KEY,
						FieldType.String));
				md.setFieldDefinitions(fieldDefinitions);
				geoEventDefinitions.put(DEFINITION_NAME, md);
			}
			
		} catch (ConfigurationException e) {
			logger.warn(e.getMessage(), e);
		}
	}

}
