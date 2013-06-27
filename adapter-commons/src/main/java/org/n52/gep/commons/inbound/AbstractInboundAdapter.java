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
package org.n52.gep.commons.inbound;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.esri.ges.adapter.AdapterDefinition;
import com.esri.ges.adapter.InboundAdapterBase;
import com.esri.ges.core.component.ComponentException;
import com.esri.ges.core.geoevent.GeoEvent;

public abstract class AbstractInboundAdapter extends InboundAdapterBase {

	private static final Logger logger = LoggerFactory.getLogger(AbstractInboundAdapter.class);
	
	public AbstractInboundAdapter(AdapterDefinition definition)
			throws ComponentException {
		super(definition);
	}

	@Override
	protected GeoEvent adapt(ByteBuffer buffer, String channel) {
		if (!allDataConsumed()) {
			return createNextRemainingGeoEvent();
		}
		
		if (!buffer.hasRemaining()) return null;
		
		buffer.mark();

		try {
			byte[] copy = new byte[buffer.remaining()];

			int i = 0;
			while (buffer.hasRemaining()) {
				copy[i++] = buffer.get();
			}
			
			InputStream bis = new ByteArrayInputStream(copy);

			return createGeoEvent(bis);
		} catch (BufferUnderflowException e) {
			buffer.reset();
			logger.warn(e.getMessage(), e);
		}

		return null;
	}

	protected abstract GeoEvent createNextRemainingGeoEvent();

	protected abstract boolean allDataConsumed();

	protected abstract GeoEvent createGeoEvent(InputStream stream);

}
