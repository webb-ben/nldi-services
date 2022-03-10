package gov.usgs.owi.nldi.transform;

import gov.usgs.owi.nldi.dao.NavigationDao;
import gov.usgs.owi.nldi.services.ConfigurationService;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import mil.nga.sf.geojson.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HydrolocationTransformer implements ITransformer {
  private static final Logger LOG = LoggerFactory.getLogger(HydrolocationTransformer.class);

  // Feature property keys
  private static final String TYPE = "type";
  private static final String SOURCE = "source";
  private static final String SOURCE_NAME = "sourceName";
  private static final String IDENTIFIER = "identifier";
  private static final String NAME = "name";
  private static final String URI = "uri";
  private static final String COMID = "comid";
  private static final String REACH_CODE = "reachcode";
  private static final String MEASURE = "measure";
  private static final String NAVIGATION = "navigation";

  private OutputStream target;
  private FeatureCollection featureCollection;
  private final ConfigurationService configurationService;

  public HydrolocationTransformer(
      HttpServletResponse response, ConfigurationService configurationService) {
    try {
      this.target = new BufferedOutputStream(response.getOutputStream());
    } catch (IOException e) {
      String msgText = "Unable to get output stream";
      LOG.error(msgText, e);
      throw new RuntimeException(msgText, e);
    }

    this.configurationService = configurationService;
    this.featureCollection = new FeatureCollection();
  }

  @Override
  public void write(Object result) {
    if (null == result) {
      return;
    }

    try {
      target.flush();
    } catch (IOException e) {
      throw new RuntimeException("Error flushing OutputStream", e);
    }
  }

  public void writeProvidedFeature(Position position) {
    Point point = new Point(position);
    Feature feature = new Feature(point);
    Map<String, Object> properties = new HashMap<>();
    properties.put(TYPE, "point");
    properties.put(SOURCE, "provided");
    properties.put(SOURCE_NAME, "Provided via API call");
    properties.put(IDENTIFIER, "");
    properties.put(NAME, "");
    properties.put(URI, "");
    properties.put(COMID, "");
    properties.put(REACH_CODE, "");
    properties.put(MEASURE, "");
    properties.put(NAVIGATION, "");
    feature.setProperties(properties);
    this.featureCollection.addFeature(feature);
  }

  public void writeIndexedFeature(
      Position position, String comid, String reachcode, String measure) {
    Point point = new Point(position);
    Feature feature = new Feature(point);
    Map<String, Object> properties = new HashMap<>();
    properties.put(TYPE, "hydrolocation");
    properties.put(SOURCE, "indexed");
    properties.put(SOURCE_NAME, "Automatically indexed by the NLDI");
    properties.put(IDENTIFIER, "");
    properties.put(NAME, "");
    properties.put(URI, "");
    properties.put(COMID, comid);
    properties.put(REACH_CODE, reachcode);
    properties.put(MEASURE, measure);
    properties.put(
        NAVIGATION,
        String.join("/", configurationService.getLinkedDataUrl(), comid, NavigationDao.NAVIGATION));
    feature.setProperties(properties);
    this.featureCollection.addFeature(feature);
  }

  @Override
  public void end() {
    try {
      target.write(
          FeatureConverter.toStringValue(featureCollection).getBytes(StandardCharsets.UTF_8));
      target.flush();
    } catch (IOException e) {
      throw new RuntimeException("Error writing FeatureCollection to OutputStream.", e);
    }
  }

  @Override
  public void close() throws Exception {
    // do nothing, just like OutputStream
  }
}
