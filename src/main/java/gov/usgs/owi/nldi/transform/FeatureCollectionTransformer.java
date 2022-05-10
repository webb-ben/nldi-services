package gov.usgs.owi.nldi.transform;

import gov.usgs.owi.nldi.services.ConfigurationService;
import java.io.IOException;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;

public class FeatureCollectionTransformer extends FeatureTransformer {

  public FeatureCollectionTransformer(
      HttpServletResponse response, ConfigurationService configurationService) {
    super(response, configurationService);
  }

  public void startCollection(Map<String, Object> resultMap) {
    super.initJson(jsonGenerator, resultMap);
  }

  public void endCollection() throws IOException {
    jsonGenerator.writeEndArray();
    jsonGenerator.writeEndObject();
  }

  public void writeFeature(Map<String, Object> resultMap) {
    super.writeMap(jsonGenerator, resultMap);
  }
}
