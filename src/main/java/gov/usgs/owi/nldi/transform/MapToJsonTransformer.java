package gov.usgs.owi.nldi.transform;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import org.apache.commons.lang3.StringEscapeUtils;

public abstract class MapToJsonTransformer extends OutputStream implements ITransformer {

	public static final String DEFAULT_ENCODING = "UTF-8";

	protected OutputStream target;

	protected boolean endPrevious = false;

	/** Is this the first write to the stream. */
	protected boolean first = true;

	public MapToJsonTransformer(OutputStream target) {
		this.target = target;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void write(Object result) {
		if (null == result) {
			return;
		}
		
		if (result instanceof Map) {
			Map<String, Object> resultMap = (Map<String, Object>) result;
			if (first) {
				writeHeader();
				first = false;
			}
			writeData(resultMap);
		}
		try {
			target.flush();
		} catch (IOException e) {
			throw new RuntimeException("Error flushing OutputStream", e);
		}
	}

	protected void writeHeader() {
		writeToStream("{\"type\":\"FeatureCollection\",\"features\":[");
	}

	protected void writeData(Map<String, Object> resultMap) {
		if (endPrevious) {
			writeToStream(",");
		}
		writeToStream("{\"type\":\"Feature\",\"geometry\":");
		writeToStream(getValue(resultMap, "shape"));
		writeToStream(",\"properties\":{");

		writeProperties(resultMap);
		
		writeToStream("}}");
		endPrevious = true;
	}

	@Override
	public void write(int b) {
		//Nothing to do here, but we need to override because we are extending OutpuStream.
		throw new RuntimeException("Writing a single byte is not supported");
	}

	/** 
	 * Converts a string to a byte array and stream it.
	 * @param in the string to be streamed.
	 */
	protected void writeToStream(final String in) {
		try {
			if (null != in) {
				target.write(in.getBytes(DEFAULT_ENCODING));
			}
		} catch (IOException e) {
			throw new RuntimeException("Error writing to stream", e);
		}
	}

	/** output the closing tag if we have written data. */
	@Override
	public void end() {
		if (!first) {
			writeToStream("]}");
		}
		try {
			target.flush();
			this.close();
		} catch (IOException e) {
			throw new RuntimeException("Error ending transformation", e);
		}
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

	abstract void writeProperties(Map<String, Object> resultMap);

}