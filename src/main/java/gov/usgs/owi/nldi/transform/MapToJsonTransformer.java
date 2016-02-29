package gov.usgs.owi.nldi.transform;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.Map;
import org.apache.commons.lang3.StringEscapeUtils;

public class MapToJsonTransformer extends Transformer {

	private boolean endPrevious = false;

	public MapToJsonTransformer(OutputStream target, Map<String, String> mapping) {
		super(target, mapping);
	}

	@Override
	protected void writeHeader() {
		writeToStream("{\"type\":\"FeatureCollection\",\"features\":[");
	}

	@Override
	protected void writeData(Map<String, Object> resultMap) {
		if (endPrevious) {
			writeToStream(",");
		}
		writeToStream("{\"type\":\"Feature\",\"geometry\":");
		writeToStream(getValue(resultMap, "shape"));
		writeToStream(",\"properties\":{\"nhdplus_comid\":\"");
		writeToStream(getValueEncode(resultMap, "nhdplus_comid"));
		writeToStream("\"}}");
		endPrevious = true;
	}
	
	/** output the closing tag. */
	@Override
	public void end() {
   		writeToStream("]}");
   		super.end();
	}

	protected String getValue(Map<String, Object> resultMap, String key) {
		if (resultMap.containsKey(key) && null != resultMap.get(key)) {
			return resultMap.get(key).toString();
		} else {
			return "";
		}
	}
	protected String getValueEncode(Map<String, Object> resultMap, String key) {
		if (resultMap.containsKey(key) && null != resultMap.get(key)) {
			return encode(resultMap.get(key).toString());
		} else {
			return "";
		}
	}

	@Override
	public String encode(String value) {
		return StringEscapeUtils.escapeJson(value);
	}

	@Override
	protected void init() {
		//Nothing to do here
	}

}
