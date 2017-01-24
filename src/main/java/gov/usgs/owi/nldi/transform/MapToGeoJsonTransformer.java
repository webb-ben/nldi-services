package gov.usgs.owi.nldi.transform;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.core.JsonGenerator;

import gov.usgs.owi.nldi.dao.BaseDao;

public abstract class MapToGeoJsonTransformer extends MapToJsonTransformer {

	public static final String COUNT_SUFFIX = "_count";
	public static final String DEFAULT_ENCODING = "UTF-8";
	public static final String FEATURE_COLLECTION = "FeatureCollection";
	public static final String FEATURE_INIT_CAP = "Feature";
	public static final String GEOMETRY = "geometry";
	public static final String PROPERTIES = "properties";
	public static final String SHAPE = "shape";
	public static final String TOTAL_ROWS = "total_rows";
	public static final String TYPE = "type";

	protected String rootUrl;
	protected String countHeaderName; //thread local??

	abstract void writeProperties(Map<String, Object> resultMap);

	public MapToGeoJsonTransformer(HttpServletResponse response, String rootUrl, String countHeaderName) {
		super(response);
		this.rootUrl = rootUrl;
		this.countHeaderName = countHeaderName;
	}

	@Override
	void addResponseHeaders(HttpServletResponse response, Map<String, Object> resultMap) {
		response.setHeader(countHeaderName, getValue(resultMap, TOTAL_ROWS));
	}

	@Override
	void initJson(JsonGenerator jsonGenerator, Map<String, Object> resultMap) {
		try {
			jsonGenerator.writeStartObject();
			jsonGenerator.writeStringField(TYPE, FEATURE_COLLECTION);
			jsonGenerator.writeFieldName(BaseDao.FEATURES);
			jsonGenerator.writeStartArray();
		} catch (IOException e) {
			throw new RuntimeException("Error starting json document", e);
		}
	}

	@Override
	void writeMap(JsonGenerator jsonGenerator, Map<String, Object> resultMap) {
		try {
			jsonGenerator.writeStartObject();

			jsonGenerator.writeStringField(TYPE, FEATURE_INIT_CAP);

			jsonGenerator.writeFieldName(GEOMETRY);
			jsonGenerator.writeStartObject();
			jsonGenerator.writeRaw(getValue(resultMap, SHAPE).replace("{", "").replace("}", ""));
			jsonGenerator.writeEndObject();

			jsonGenerator.writeObjectFieldStart(PROPERTIES);
			writeProperties(resultMap);
			jsonGenerator.writeEndObject();

			jsonGenerator.writeEndObject();
		} catch (IOException e) {
			throw new RuntimeException("Error writing json", e);
		}
	}
}
