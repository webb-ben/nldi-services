package gov.usgs.owi.nldi.transform;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

import gov.usgs.owi.nldi.dao.BaseDao;

public abstract class MapToGeoJsonTransformer extends OutputStream implements ITransformer {
	private static final Logger LOG = LoggerFactory.getLogger(MapToGeoJsonTransformer.class);

	public static final String COUNT_SUFFIX = "_count";
	public static final String DEFAULT_ENCODING = "UTF-8";
	public static final String FEATURE_COLLECTION = "FeatureCollection";
	public static final String FEATURE_INIT_CAP = "Feature";
	public static final String GEOMETRY = "geometry";
	public static final String PROPERTIES = "properties";
	public static final String SHAPE = "shape";
	public static final String TOTAL_ROWS = "total_rows";
	public static final String TYPE = "type";

	protected OutputStream target;
	protected String rootUrl;
	protected JsonFactory f;
	protected JsonGenerator g;
	protected boolean firstRow = true; //thread local??
	protected String countHeaderName; //thread local??
	protected HttpServletResponse response;

	public MapToGeoJsonTransformer(HttpServletResponse response, String rootUrl, String countHeaderName) {
		try {
			this.target = new BufferedOutputStream(response.getOutputStream());
		} catch (IOException e) {
			String msgText = "Unable to get output stream";
			LOG.error(msgText, e);
			throw new RuntimeException(msgText, e);
		}
		this.rootUrl = rootUrl;
		this.firstRow = true;
		this.response = response;
		this.countHeaderName = countHeaderName;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void write(Object result) {
		if (null == result) {
			return;
		}

		if (result instanceof Map) {
			Map<String, Object> resultMap = (Map<String, Object>) result;
			if (firstRow) {
				init(response, countHeaderName, resultMap);
				firstRow = false;
			}
			writeData(resultMap);
		}
		try {
			target.flush();
		} catch (IOException e) {
			throw new RuntimeException("Error flushing OutputStream", e);
		}
	}

	protected void init(HttpServletResponse response, String countHeaderName, Map<String, Object> resultMap) {
		response.setHeader(countHeaderName, getValue(resultMap, TOTAL_ROWS));
		f = new JsonFactory();
		try {
			g = f.createGenerator(target);
			g.writeStartObject();
			g.writeStringField(TYPE, FEATURE_COLLECTION);
			g.writeFieldName(BaseDao.FEATURES);
			g.writeStartArray();
		} catch (IOException e) {
			throw new RuntimeException("Error starting json document", e);
		}
	}

	protected void writeData(Map<String, Object> resultMap) {
		try {
			g.writeStartObject();

			g.writeStringField(TYPE, FEATURE_INIT_CAP);

			g.writeFieldName(GEOMETRY);
			g.writeStartObject();
			g.writeRaw(getValue(resultMap, SHAPE).replace("{", "").replace("}", ""));
			g.writeEndObject();

			g.writeObjectFieldStart(PROPERTIES);
			writeProperties(resultMap);
			g.writeEndObject();

			g.writeEndObject();
		} catch (IOException e) {
			throw new RuntimeException("Error writing json", e);
		}
	}

	@Override
	public void write(int b) {
		//Nothing to do here, but we need to override because we are extending OutpuStream.
		throw new RuntimeException("Writing a single byte is not supported");
	}

	/** output the closing tags and flush the stream. */
	@Override
	public void end() {
		try {
			if (null != g) {
				g.close();
			}
			target.flush();
		} catch (IOException e) {
			throw new RuntimeException("Error ending json document", e);
		}
	}

	protected String getValue(Map<String, Object> resultMap, String key) {
		if (resultMap.containsKey(key) && null != resultMap.get(key)) {
			return resultMap.get(key).toString();
		} else {
			return "";
		}
	}

	abstract void writeProperties(Map<String, Object> resultMap);

}
