package gov.usgs.owi.nldi.transform;

import java.io.OutputStream;
import java.util.Map;

public class FeatureTransformer extends MapToJsonTransformer {

	public FeatureTransformer(OutputStream target) {
		super(target);
	}

	@Override
	protected void writeProperties(Map<String, Object> resultMap) {
		writeToStream("\"comid\":\"");
		writeToStream(getValueEncode(resultMap, "comid"));
		writeToStream("\",");

		writeToStream("\"identifier\":\"");
		writeToStream(getValueEncode(resultMap, "identifier"));
		writeToStream("\",");

		writeToStream("\"name\":\"");
		writeToStream(getValueEncode(resultMap, "name"));
		writeToStream("\",");

		writeToStream("\"uri\":\"");
		writeToStream(getValueEncode(resultMap, "uri"));
		writeToStream("\"");
	}
	
}
