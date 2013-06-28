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
package org.n52.cario.gep.commons.processor;

import org.n52.cario.gep.commons.annotations.Processor;

import com.esri.ges.core.component.ComponentException;
import com.esri.ges.processor.GeoEventProcessorBase;
import com.esri.ges.processor.GeoEventProcessorDefinition;
import com.esri.ges.processor.GeoEventProcessorDefinitionBase;

public abstract class AbstractAnnotatedProcessor extends GeoEventProcessorBase {

	public AbstractAnnotatedProcessor(GeoEventProcessorDefinition definition)
			throws ComponentException {
		super(definition);
	}
	
	protected static GeoEventProcessorDefinition createDefinition(Class<? extends AbstractAnnotatedProcessor> c) {
		return new DefinitionThroughAnnotation(c);
	}

	public static class DefinitionThroughAnnotation extends GeoEventProcessorDefinitionBase {
		
		private Processor anno;

		public DefinitionThroughAnnotation(Class<? extends AbstractAnnotatedProcessor> c) {
			this.anno = c.getAnnotation(Processor.class);
		}
		
		@Override
		public String getName() {
			return  anno.name();
		}

		@Override
		public String getDomain() {
			return anno.domain();
		}

		@Override
		public String getVersion() {
			return anno.version();
		}

		@Override
		public String getLabel() {
			return anno.label();
		}

		@Override
		public String getDescription() {
			return anno.description();
		}

		@Override
		public String getContactInfo() {
			return anno.contactInfo();
		}
		
	}
}
