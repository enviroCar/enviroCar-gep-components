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
package org.n52.cario.parser.sensor;

import java.util.Map;

public class SensorParser {

	private static final String TYPE_KEY = "type";

	private static final String HREF_KEY = "href";

	private static final String PROPERTIES_KEY = "properties";

	private static final String ID_KEY = "id";

	private static final String MODEL_KEY = "model";

	private static final String FUEL_TYPE_KEY = "fuelType";

	private static final String MANUFACTURER_KEY = "manufacturer";
	
	private String id;
	private String model;
	private String fuelType;
	private String manufacturer;
	private String href;
	private String type;

	public SensorParser() {
	}

	public void parse(Object object) {
		if (object instanceof Map<?, ?>) {
			parseMap((Map<?, ?>) object);
		}
		else if (object instanceof String) {
			this.id = object.toString();
		}
		
	}

	private void parseMap(Map<?, ?> map) {
		if (map.containsKey(TYPE_KEY)) {
			this.type = (String) map.get(TYPE_KEY);
		}
		if (map.containsKey(HREF_KEY)) {
			this.href = (String) map.get(HREF_KEY);
		}
		if (map.containsKey(PROPERTIES_KEY)) {
			Map<?, ?> props = (Map<?, ?>) map.get(PROPERTIES_KEY);
			parseProperties(props);
		}
	}

	private void parseProperties(Map<?, ?> props) {
		if (props.containsKey(ID_KEY)) {
			this.id = (String) props.get(ID_KEY);
		}
		if (props.containsKey(MODEL_KEY))  {
			this.model = (String) props.get(MODEL_KEY);
		}
		if (props.containsKey(FUEL_TYPE_KEY)) {
			this.fuelType = (String) props.get(FUEL_TYPE_KEY);
		}
		if (props.containsKey(MANUFACTURER_KEY)) {
			this.manufacturer = (String) props.get(MANUFACTURER_KEY);
		}
	}

	public String getModel() {
		return model;
	}

	public String getFuelType() {
		return fuelType;
	}

	public String getManufacturer() {
		return manufacturer;
	}

	public String getHref() {
		return href;
	}

	public String getType() {
		return type;
	}

	public String getId() {
		return this.id;
	}

	
}
