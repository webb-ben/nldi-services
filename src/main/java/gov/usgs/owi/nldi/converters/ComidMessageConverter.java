package gov.usgs.owi.nldi.converters;

import gov.usgs.owi.nldi.controllers.BaseController;
import gov.usgs.owi.nldi.model.Comid;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import mil.nga.sf.geojson.FeatureCollection;
import mil.nga.sf.geojson.FeatureConverter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

public class ComidMessageConverter extends AbstractHttpMessageConverter<Comid> {

  public ComidMessageConverter(MediaType... supportedMediaTypes) {
    super(supportedMediaTypes);
  }

  @Override
  protected boolean supports(Class<?> aClass) {
    return Comid.class.isAssignableFrom(aClass);
  }

  @Override
  protected Comid readInternal(Class<? extends Comid> aClass, HttpInputMessage httpInputMessage)
      throws IOException, HttpMessageNotReadableException {
    return null;
  }

  @Override
  protected void writeInternal(Comid comid, HttpOutputMessage httpOutputMessage)
      throws IOException, HttpMessageNotWritableException {
    String contentType = httpOutputMessage.getHeaders().getContentType().toString();
    switch (contentType) {
      case BaseController.MIME_TYPE_GEOJSON:
      case MediaType.APPLICATION_JSON_VALUE:
        writeGeoJson(comid, httpOutputMessage);
        break;
    }
  }

  private void writeGeoJson(Comid comid, HttpOutputMessage httpOutputMessage) throws IOException {

    mil.nga.sf.geojson.Feature geoJsonFeature = new mil.nga.sf.geojson.Feature();
    geoJsonFeature.setGeometry(comid.getGeometry());

    Map<String, Object> properties = new HashMap<>();
    properties.put("source", comid.getSource());
    properties.put("sourceName", comid.getSourceName());
    properties.put("identifier", comid.getIdentifier());
    properties.put("comid", comid.getComid().toString());
    properties.put("navigation", comid.getNavigation());

    geoJsonFeature.setProperties(properties);

    FeatureCollection collection = new FeatureCollection(geoJsonFeature);
    String geoJsonString = FeatureConverter.toStringValue(collection);

    byte[] output = geoJsonString.getBytes(StandardCharsets.UTF_8);
    httpOutputMessage.getBody().write(output);
  }
}
