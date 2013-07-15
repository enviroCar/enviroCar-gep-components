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
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.X509Certificate;
import java.util.Scanner;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.BasicClientConnectionManager;
import org.apache.http.util.EntityUtils;

public class TrackPublisher {
	
	protected String targetConsumer;
	
	public TrackPublisher() {
		this("https://ags.dev.52north.org:6143/geoevent/rest/receiver/f9041d70-e7cb-11e2-91e2-0800200c9a66");
	}
	
	public TrackPublisher(String consumer) {
		this.targetConsumer = consumer;
	}
	
	protected void pushToConsumer(String content)
			throws ClientProtocolException, IOException {
		HttpClient client = createAllTrustingClient();
		HttpPost post = new HttpPost(targetConsumer);
		post.setEntity(new StringEntity(content, ContentType.APPLICATION_JSON));
		HttpResponse resp = client.execute(post);
		EntityUtils.consume(resp.getEntity());
	}
	
	protected HttpClient createAllTrustingClient() throws IOException {
		SSLSocketFactory sslsf;
		try {
			sslsf = new SSLSocketFactory(new TrustStrategy() {
				@Override
				public boolean isTrusted(final X509Certificate[] chain,
						String authType) {
					// FIXME kind of bad practice...
					return true;
				}
			}, new AllowAllHostnameVerifier());
		} catch (KeyManagementException e) {
			throw new IOException(e);
		} catch (UnrecoverableKeyException e) {
			throw new IOException(e);
		} catch (NoSuchAlgorithmException e) {
			throw new IOException(e);
		} catch (KeyStoreException e) {
			throw new IOException(e);
		}
		Scheme httpsScheme2 = new Scheme("https", 443, sslsf);
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(httpsScheme2);

		BasicClientConnectionManager cm = new BasicClientConnectionManager(
				schemeRegistry);

		return new DefaultHttpClient(cm);
	}
	
	protected String readContent(InputStream content) throws IOException {
		Scanner sc = new Scanner(content);
		StringBuilder sb = new StringBuilder(content.available());
		while (sc.hasNext()) {
			sb.append(sc.nextLine());
		}
		sc.close();
		return sb.toString();
	}

}
