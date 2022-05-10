package gov.usgs.owi.nldi.transform;

import com.fasterxml.jackson.core.JsonGenerator;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;

public class BasinTransformer extends MapToGeoJsonTransformer {

  public BasinTransformer(HttpServletResponse response) {
    super(response);
  }

  @Override
  protected void writeProperties(JsonGenerator jsonGenerator, Map<String, Object> resultMap) {
    // Nothing to do for this one.
  }
}
