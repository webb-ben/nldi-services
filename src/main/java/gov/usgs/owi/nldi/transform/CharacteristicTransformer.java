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

public class CharacteristicTransformer extends OutputStream implements ITransformer {
	private static final Logger LOG = LoggerFactory.getLogger(CharacteristicTransformer.class);

	public static final String CHARACTERISTIC = "characteristic";

	public static final String CHARACTERISTIC_ID = "characteristic_id";
	public static final String CHARACTERISTIC_DESCRIPTION = "characteristic_description";
	public static final String UNIT = "units";
	public static final String DATASET_LABEL = "dataset_label";
	public static final String DATASET_URL = "dataset_url";
	public static final String THEME_LABEL = "theme_label";
	public static final String THEME_URL = "theme_url";
	public static final String CHARACTERISTIC_TYPE = "characteristic_type";
	
	protected OutputStream target;
	protected JsonFactory f;
	protected JsonGenerator g;
	protected HttpServletResponse response;
	
	public CharacteristicTransformer(HttpServletResponse response, String rootUrl) {
		try {
			this.target = new BufferedOutputStream(response.getOutputStream());
		} catch (IOException e) {
			String msgText = "Unable to get output stream";
			LOG.error(msgText, e);
			throw new RuntimeException(msgText, e);
		}
		init();
	}

	protected void init() {
		f = new JsonFactory();
		try {
			g = f.createGenerator(target);
			g.writeStartObject();
			g.writeFieldName(BaseDao.CHARACTERISTICS);
			g.writeStartArray();
		} catch (IOException e) {
			throw new RuntimeException("Error starting json document", e);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void write(Object result) {
		if (null == result) {
			return;
		}

		if (result instanceof Map) {
			Map<String, Object> resultMap = (Map<String, Object>) result;
			writeData(resultMap);
		}
		try {
			target.flush();
		} catch (IOException e) {
			throw new RuntimeException("Error flushing OutputStream", e);
		}
	}
	
	protected void writeData(Map<String, Object> resultMap) {
		try {
			g.writeStartObject();

			g.writeObjectFieldStart(CHARACTERISTIC);
			writeProperties(resultMap);
			g.writeEndObject();

			g.writeEndObject();
		} catch (IOException e) {
			throw new RuntimeException("Error writing json", e);
		}
	}

	protected void writeProperties(Map<String, Object> resultMap) {
		try {
			g.writeStringField(CHARACTERISTIC_ID, getValue(resultMap, CHARACTERISTIC_ID));
			g.writeStringField(CHARACTERISTIC_DESCRIPTION, getValue(resultMap, CHARACTERISTIC_DESCRIPTION));
			g.writeStringField(UNIT, getValue(resultMap, UNIT));
			g.writeStringField(DATASET_LABEL, getValue(resultMap, DATASET_LABEL));
			g.writeStringField(DATASET_URL, getValue(resultMap, DATASET_URL));
			g.writeStringField(THEME_LABEL, getValue(resultMap, THEME_LABEL));
			g.writeStringField(THEME_URL, getValue(resultMap, THEME_URL));
			g.writeStringField(CHARACTERISTIC_TYPE, getValue(resultMap, CHARACTERISTIC_TYPE));
		} catch (IOException e) {
			throw new RuntimeException("Error writing properties", e);
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
}
