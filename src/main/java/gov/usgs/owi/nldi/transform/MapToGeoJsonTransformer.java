package gov.usgs.owi.nldi.transform;

import com.fasterxml.jackson.core.JsonGenerator;
import gov.usgs.owi.nldi.dao.BaseDao;
import java.io.IOException;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class MapToGeoJsonTransformer extends MapToJsonTransformer {
  private static final Logger LOG = LoggerFactory.getLogger(MapToGeoJsonTransformer.class);

  public static final String SHAPE = "shape";

  private static final String FEATURE_COLLECTION = "FeatureCollection";
  private static final String FEATURE_INIT_CAP = "Feature";
  private static final String GEOMETRY = "geometry";
  private static final String PROPERTIES = "properties";
  private static final String TYPE = "type";

  abstract void writeProperties(JsonGenerator jsonGenerator, Map<String, Object> resultMap);

  public MapToGeoJsonTransformer(HttpServletResponse response) {
    super(response);
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
      writeProperties(jsonGenerator, resultMap);
      jsonGenerator.writeEndObject();

      jsonGenerator.writeEndObject();
    } catch (IOException e) {
      throw new RuntimeException("Error writing json", e);
    }
  }
}
