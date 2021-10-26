package gov.usgs.owi.nldi.transform;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import gov.usgs.owi.nldi.dao.BaseDao;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public class SplitCatchmentTransformer implements ITransformer {
	private static final Logger LOG = LoggerFactory.getLogger(SplitCatchmentTransformer.class);

	private static final String FEATURE_COLLECTION = "FeatureCollection";
	private static final String FEATURE_INIT_CAP = "Feature";
	private static final String GEOMETRY = "geometry";
	private static final String PROPERTIES = "properties";
	private static final String TYPE = "type";
	private static final String FEATURES = "features";
	private static final String ID = "id";
	private static final String OUTPUTS = "outputs";

	private OutputStream target;
	private JsonFactory factory;
	private HttpServletResponse response;

	private JsonGenerator generator;

	public SplitCatchmentTransformer(HttpServletResponse response) {
		try {
			this.target = new BufferedOutputStream(response.getOutputStream());
		} catch (IOException e) {
			String msgText = "Unable to get output stream";
			LOG.error(msgText, e);
			throw new RuntimeException(msgText, e);
		}
		this.response = response;
		factory = new JsonFactory();
		try {
			generator = factory.createGenerator(target);
		} catch (IOException e) {
			throw new RuntimeException("Error building json generator", e);
		}
	}

	@Override
	public void write(Object result) {
		if (null == result) {
			return;
		}

		if (result instanceof JSONObject) {
			JSONObject content = (JSONObject)result;
			try {
				writeSplitCatchmentContent(content);
			} catch (Exception e) {
				throw new RuntimeException("Unable to write split-catchment response to JSON", e);
			}
		}

		try {
			target.flush();
		} catch (IOException e) {
			throw new RuntimeException("Error flushing OutputStream", e);
		}
	}

	private void writeSplitCatchmentContent(JSONObject content) throws IOException, JSONException {
		JSONArray features = content.getJSONObject(OUTPUTS).getJSONArray(FEATURES);

		generator.writeStartObject();
		generator.writeStringField(TYPE, FEATURE_COLLECTION);
		generator.writeArrayFieldStart(FEATURES);

		JSONObject currentObject;
		for (int i = features.length() - 1; i >= 0; i--) {
			currentObject = features.getJSONObject(i);
			if (currentObject.has(ID) && currentObject.getString(ID).equals("mergedCatchment")) {
				// remove the mergedCatchment id as it's not needed and better matches expected output
				currentObject.remove(ID);
				generator.writeRaw(currentObject.toString());
				break;
			}
		}

		generator.writeEndArray();
		generator.writeEndObject();
	}

	@Override
	public void end() {
		try {
			if (null != generator) {
				generator.close();
			}
			target.flush();
		} catch (IOException e) {
			throw new RuntimeException("Error ending json document", e);
		}
	}

	@Override
	public void close() throws Exception {
		// do nothing, just like OutputStream
	}
}
