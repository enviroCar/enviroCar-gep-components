package org.n52.gep.inbound.cario;

import com.esri.ges.adapter.Adapter;
import com.esri.ges.adapter.AdapterServiceBase;
import com.esri.ges.core.component.ComponentException;

public class TrackInboundAdapterService extends AdapterServiceBase {
	
	public TrackInboundAdapterService() {
		definition = new TrackInboundDefinition();
	}

	@Override
	public Adapter createAdapter() throws ComponentException {
		return new TrackInboundAdapter(definition);
	}
}
