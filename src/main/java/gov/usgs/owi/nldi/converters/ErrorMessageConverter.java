package gov.usgs.owi.nldi.converters;

import gov.usgs.owi.nldi.controllers.BaseController;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

public class ErrorMessageConverter extends AbstractHttpMessageConverter<Error> {

  public ErrorMessageConverter(MediaType... supportedMediaTypes) {
    super(supportedMediaTypes);
  }

  @Override
  protected boolean supports(Class<?> aClass) {
    return Error.class.isAssignableFrom(aClass);
  }

  @Override
  protected Error readInternal(Class<? extends Error> aClass, HttpInputMessage httpInputMessage)
      throws IOException, HttpMessageNotReadableException {
    return null;
  }

  @Override
  protected void writeInternal(Error error, HttpOutputMessage httpOutputMessage)
      throws IOException, HttpMessageNotWritableException {
    String contentType = httpOutputMessage.getHeaders().getContentType().toString();
    switch (contentType) {
      case BaseController.MIME_TYPE_JSONLD:
        writeJsonLd(error, httpOutputMessage);
        break;
      case BaseController.MIME_TYPE_GEOJSON:
      case MediaType.APPLICATION_JSON_VALUE:
        writeGeoJson(error, httpOutputMessage);
        break;
      default:
        // write out error as string (no formatting)
        httpOutputMessage.getBody().write(error.getMessage().getBytes(StandardCharsets.UTF_8));
        break;
    }
  }

  private void writeJsonLd(Error error, HttpOutputMessage httpOutputMessage) throws IOException {
    try {
      JSONObject errorObject = new JSONObject();
      errorObject.put("@context", "https://schema.org");
      errorObject.put("@type", "error");
      errorObject.put("description", error.getMessage().replaceAll("\"", "'"));
      byte[] output = errorObject.toString().getBytes(StandardCharsets.UTF_8);
      httpOutputMessage.getBody().write(output);
    } catch (JSONException exception) {
      throw new RuntimeException("Failed to write error to JSON-LD");
    }
  }

  private void writeGeoJson(Error error, HttpOutputMessage httpOutputMessage) throws IOException {
    try {
      JSONObject errorObject = new JSONObject();
      errorObject.put("type", "error");
      errorObject.put("description", error.getMessage().replaceAll("\"", "'"));

      byte[] output = errorObject.toString().getBytes(StandardCharsets.UTF_8);
      httpOutputMessage.getBody().write(output);
    } catch (JSONException exception) {
      throw new RuntimeException("Failed to write error to GeoJSON");
    }
  }
}
