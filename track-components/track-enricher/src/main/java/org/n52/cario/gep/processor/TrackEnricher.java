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
package org.n52.cario.gep.processor;

import org.n52.cario.gep.commons.annotations.Processor;
import org.n52.cario.gep.commons.processor.AbstractAnnotatedProcessor;

import com.esri.ges.core.component.ComponentException;
import com.esri.ges.core.geoevent.GeoEvent;
import com.esri.ges.processor.GeoEventProcessorDefinition;

@Processor(name = "enviroCarTrackEnricher",
	contactInfo = "info@52north.org",
	description = "Enrich Track GeoEvent through fetching referenced resources",
	domain = "com.esri.ges.processor",
	label = "enviroCar Track Enricher",
	version = "0.1.0")
public class TrackEnricher extends AbstractAnnotatedProcessor {

	public TrackEnricher(GeoEventProcessorDefinition definition)
			throws ComponentException {
		super(definition);
	}

	@Override
	public GeoEvent process(GeoEvent event) throws Exception {
		return event;
	}

}
