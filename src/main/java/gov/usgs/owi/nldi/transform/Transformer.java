package gov.usgs.owi.nldi.transform;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.Map.Entry;

public abstract class Transformer extends OutputStream implements ITransformer {
	String DEFAULT_ENCODING = "UTF-8";

	protected OutputStream target;
	protected Map<String, String> mapping;

	/** Is this the first write to the stream. */
	private boolean first = true;

	public Transformer(OutputStream target, Map<String, String> mapping) {
		this.target = target;
		this.mapping = mapping;
	}
	
	protected String getMappedName(Entry<?, ?> entry) {
		return mapping.get(entry.getKey());
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
				init();
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
	
	protected abstract void init();
	
	protected abstract void writeHeader();

	protected abstract void writeData(Map<String, Object> resultMap);

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

	public void end() {
		try {
			target.flush();
			this.close();
		} catch (IOException e) {
			throw new RuntimeException("Error ending transformation", e);
		}
	}

}
