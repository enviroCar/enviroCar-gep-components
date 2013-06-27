package org.n52.gep.commons.json;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtil {

	public static Map<?, ?> createJson(InputStream input) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(input, Map.class);
	}
	
}
