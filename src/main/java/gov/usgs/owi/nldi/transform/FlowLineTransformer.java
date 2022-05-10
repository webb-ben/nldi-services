package gov.usgs.owi.nldi.transform;

import com.fasterxml.jackson.core.JsonGenerator;
import java.io.IOException;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;

public class FlowLineTransformer extends MapToGeoJsonTransformer {

  protected static final String NHDPLUS_COMID = "nhdplus_comid";

  public FlowLineTransformer(HttpServletResponse response) {
    super(response);
  }

  @Override
  protected void writeProperties(JsonGenerator jsonGenerator, Map<String, Object> resultMap) {
    try {
      jsonGenerator.writeStringField(NHDPLUS_COMID, getValue(resultMap, NHDPLUS_COMID));
    } catch (IOException e) {
      throw new RuntimeException("Error writing properties", e);
    }
  }
}
