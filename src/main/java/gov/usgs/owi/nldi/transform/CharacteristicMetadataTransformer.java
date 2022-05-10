package gov.usgs.owi.nldi.transform;

import com.fasterxml.jackson.core.JsonGenerator;
import gov.usgs.owi.nldi.dao.BaseDao;
import java.io.IOException;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;

public class CharacteristicMetadataTransformer extends MapToJsonTransformer {
  protected static final String CHARACTERISTIC = "characteristic";
  protected static final String CHARACTERISTIC_ID = "characteristic_id";
  protected static final String CHARACTERISTIC_DESCRIPTION = "characteristic_description";
  protected static final String UNIT = "units";
  protected static final String DATASET_LABEL = "dataset_label";
  protected static final String DATASET_URL = "dataset_url";
  protected static final String THEME_LABEL = "theme_label";
  protected static final String THEME_URL = "theme_url";
  protected static final String CHARACTERISTIC_TYPE = "characteristic_type";

  public CharacteristicMetadataTransformer(HttpServletResponse response) {
    super(response);
  }

  @Override
  void initJson(JsonGenerator g, Map<String, Object> resultMap) {
    try {
      g.writeStartObject();
      g.writeFieldName(BaseDao.CHARACTERISTICS_METADATA);
      g.writeStartArray();
    } catch (IOException e) {
      throw new RuntimeException("Error writing json", e);
    }
  }

  @Override
  void writeMap(JsonGenerator g, Map<String, Object> resultMap) {
    try {
      g.writeStartObject();
      g.writeObjectFieldStart(CHARACTERISTIC);
      g.writeStringField(CHARACTERISTIC_ID, getValue(resultMap, CHARACTERISTIC_ID));
      g.writeStringField(
          CHARACTERISTIC_DESCRIPTION, getValue(resultMap, CHARACTERISTIC_DESCRIPTION));
      g.writeStringField(UNIT, getValue(resultMap, UNIT));
      g.writeStringField(DATASET_LABEL, getValue(resultMap, DATASET_LABEL));
      g.writeStringField(DATASET_URL, getValue(resultMap, DATASET_URL));
      g.writeStringField(THEME_LABEL, getValue(resultMap, THEME_LABEL));
      g.writeStringField(THEME_URL, getValue(resultMap, THEME_URL));
      g.writeStringField(CHARACTERISTIC_TYPE, getValue(resultMap, CHARACTERISTIC_TYPE));
      g.writeEndObject();
      g.writeEndObject();
    } catch (IOException e) {
      throw new RuntimeException("Error writing json", e);
    }
  }
}
