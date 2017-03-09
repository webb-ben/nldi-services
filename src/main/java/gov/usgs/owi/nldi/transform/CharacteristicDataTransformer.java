package gov.usgs.owi.nldi.transform;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.core.JsonGenerator;

import gov.usgs.owi.nldi.dao.BaseDao;

public class CharacteristicDataTransformer extends MapToJsonTransformer {
	protected static final String CHARACTERISTICS = "characteristics";
	protected static final String CHARACTERISTIC_ID = "characteristic_id";
	protected static final String CHARACTERISTIC_VALUE = "characteristic_value";
	protected static final String PERCENT_NO_DATA = "percent_nodata";
	
	public CharacteristicDataTransformer(HttpServletResponse response) {
		super(response);
	}
	
	@Override
	void addResponseHeaders(HttpServletResponse response, Map<String, Object> resultMap) {
	}

	@Override
	void initJson(JsonGenerator g, Map<String, Object> resultMap) {
		try {
			g.writeStartObject();																	// {
			g.writeStringField(BaseDao.COMID, getValue(resultMap, BaseDao.COMID));					// comid : comid
			g.writeFieldName(CHARACTERISTICS);														// characteristics: 
			g.writeStartArray();																	// [
		} catch (IOException e) {
			throw new RuntimeException("Error writing json", e);
		}
	}

	@Override
	void writeMap(JsonGenerator g, Map<String, Object> resultMap) {
		try {
			g.writeStartObject();																	// {
			g.writeStringField(CHARACTERISTIC_ID, getValue(resultMap, CHARACTERISTIC_ID));			// characteristic_id : b
			g.writeStringField(CHARACTERISTIC_VALUE, getValue(resultMap, CHARACTERISTIC_VALUE));	// characteristic_value : b
			g.writeStringField(PERCENT_NO_DATA, getValue(resultMap, PERCENT_NO_DATA));				// percent_nodata : b
			g.writeEndObject();																		// }
		} catch (IOException e) {
			throw new RuntimeException("Error writing json", e);
		}
	}
}
