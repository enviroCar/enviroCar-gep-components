package org.n52.gep.inbound.cario;

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

public class TrackInboundDefinition extends AdapterDefinitionBase {

	public static final String DEFINITION_NAME = "car-io-track-definition";
	
	private static final Logger logger = LoggerFactory
			.getLogger(TrackInboundDefinition.class);

	public static final String TYPE_KEY = "type";
	public static final String SENSOR_KEY = "sensor";
	public static final String GEOMETRY_KEY = "geometry";
	public static final String TIME_KEY = "time";
	public static final String CONSUMPTION_KEY = "Consumption";
	public static final String CO2_KEY = "CO2";
	public static final String SPEED_KEY = "Speed";
	public static final String MAF_KEY = "MAF";

	public TrackInboundDefinition() {
		super(AdapterType.INBOUND);
		try {
			GeoEventDefinition md = new DefaultGeoEventDefinition();
			md.setName(DEFINITION_NAME);
			List<FieldDefinition> fieldDefinitions = new ArrayList<FieldDefinition>();
			fieldDefinitions.add(new DefaultFieldDefinition(TYPE_KEY,
					FieldType.String));
			fieldDefinitions.add(new DefaultFieldDefinition(SENSOR_KEY,
					FieldType.String));
			fieldDefinitions.add(new DefaultFieldDefinition(GEOMETRY_KEY,
					FieldType.Geometry));
			fieldDefinitions.add(new DefaultFieldDefinition(TIME_KEY,
					FieldType.Date));
			fieldDefinitions.add(new DefaultFieldDefinition(CONSUMPTION_KEY,
					FieldType.Double));
			fieldDefinitions.add(new DefaultFieldDefinition(CO2_KEY,
					FieldType.Double));
			fieldDefinitions.add(new DefaultFieldDefinition(SPEED_KEY,
					FieldType.Double));
			fieldDefinitions.add(new DefaultFieldDefinition(MAF_KEY,
					FieldType.Double));
			md.setFieldDefinitions(fieldDefinitions);
			geoEventDefinitions.put(md.getName(), md);
		} catch (ConfigurationException e) {
			logger.warn(e.getMessage(), e);
		}
	}

}
