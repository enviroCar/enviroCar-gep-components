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
package org.n52.cario.gep.commons.harvest;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.n52.cario.gep.commons.json.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TrackHarvester extends TrackPublisher {

	private static final Logger logger = LoggerFactory
			.getLogger(TrackHarvester.class);

	private String baseTracks = "https://giv-car.uni-muenster.de/dev/rest/tracks/";
	
	public void harvestTracks() throws ClientProtocolException,
			IOException {
		HttpClient client = createAllTrustingClient();
		HttpResponse resp = client.execute(new HttpGet(baseTracks));
		Map<?, ?> json = JsonUtil.createJson(resp.getEntity().getContent());
		parseAndPushTracks(json);
	}

	private void parseAndPushTracks(Map<?, ?> json)
			throws ClientProtocolException, IOException {
		List<?> tracks = (List<?>) json.get("tracks");

		for (Object t : tracks) {
			String id = (String) ((Map<?, ?>) t).get("id");
			readAndPushTrack(id);
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	private void readAndPushTrack(String id)
			throws ClientProtocolException, IOException {
		DefaultHttpClient client = new DefaultHttpClient();
		HttpResponse resp = client.execute(new HttpGet(baseTracks.concat(id)));
		String content = readContent(resp.getEntity().getContent());

		logger.info("Pushing track '{}' to {}.", id, targetConsumer);
		pushToConsumer(content);
	}
	


}
