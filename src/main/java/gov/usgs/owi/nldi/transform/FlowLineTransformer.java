package gov.usgs.owi.nldi.transform;

import java.io.OutputStream;
import java.util.Map;

public class FlowLineTransformer extends MapToJsonTransformer {

	public FlowLineTransformer(OutputStream target) {
		super(target);
	}

	@Override
	protected void writeProperties(Map<String, Object> resultMap) {
		writeToStream("\"nhdplus_comid\":\"");
		writeToStream(getValueEncode(resultMap, "nhdplus_comid"));
		writeToStream("\"");
	}

}
