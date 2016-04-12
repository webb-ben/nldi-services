package gov.usgs.owi.nldi.transform;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public class FlowLineTransformer extends MapToJsonTransformer {

	public FlowLineTransformer(OutputStream target) {
		super(target);
	}

	@Override
	protected void writeProperties(Map<String, Object> resultMap) {
		try {
			g.writeStringField("nhdplus_comid", getValue(resultMap, "nhdplus_comid"));
		} catch (IOException e) {
			throw new RuntimeException("Error writing json for FlowLine Properties", e);
		}
	}

}
