package gov.usgs.owi.nldi.transform;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.core.JsonGenerator;

import gov.usgs.owi.nldi.dao.BaseDao;

public class CharacteristicMetadataTransformer extends MapToJsonTransformer {
	private static final String CHARACTERISTIC = "characteristic";
	private static final String CHARACTERISTIC_ID = "characteristic_id";
	private static final String CHARACTERISTIC_DESCRIPTION = "characteristic_description";
	private static final String UNIT = "units";
	private static final String DATASET_LABEL = "dataset_label";
	private static final String DATASET_URL = "dataset_url";
	private static final String THEME_LABEL = "theme_label";
	private static final String THEME_URL = "theme_url";
	private static final String CHARACTERISTIC_TYPE = "characteristic_type";
	
	public CharacteristicMetadataTransformer(HttpServletResponse response) {
		super(response);
	}
	
	@Override
	void addResponseHeaders(HttpServletResponse response, Map<String, Object> resultMap) {
	}

	@Override
	void initJson(JsonGenerator g, Map<String, Object> resultMap) {
		try {
			g.writeStartObject();																			// {
			g.writeFieldName(BaseDao.CHARACTERISTICS);														// chars: 
			g.writeStartArray();																			// [
		} catch (IOException e) {
			throw new RuntimeException("Error writing json", e);
		}
	}

	@Override
	void writeMap(JsonGenerator g, Map<String, Object> resultMap) {
		try {
			g.writeStartObject();																			// {
			g.writeObjectFieldStart(CHARACTERISTIC);														// char : {
			g.writeStringField(CHARACTERISTIC_ID, getValue(resultMap, CHARACTERISTIC_ID));					// a : b
			g.writeStringField(CHARACTERISTIC_DESCRIPTION, getValue(resultMap, CHARACTERISTIC_DESCRIPTION));// a : b
			g.writeStringField(UNIT, getValue(resultMap, UNIT));											// a : b
			g.writeStringField(DATASET_LABEL, getValue(resultMap, DATASET_LABEL));							// a : b
			g.writeStringField(DATASET_URL, getValue(resultMap, DATASET_URL));								// a : b
			g.writeStringField(THEME_LABEL, getValue(resultMap, THEME_LABEL));								// a : b
			g.writeStringField(THEME_URL, getValue(resultMap, THEME_URL));									// a : b
			g.writeStringField(CHARACTERISTIC_TYPE, getValue(resultMap, CHARACTERISTIC_TYPE));				// a : b
			g.writeEndObject();																				// }
			g.writeEndObject();																				// }
		} catch (IOException e) {
			throw new RuntimeException("Error writing json", e);
		}
	}
}
