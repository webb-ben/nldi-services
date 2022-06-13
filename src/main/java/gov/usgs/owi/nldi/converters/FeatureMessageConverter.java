package gov.usgs.owi.nldi.converters;

import com.fasterxml.jackson.core.JsonFactoryBuilder;
import com.fasterxml.jackson.core.JsonGenerator;
import gov.usgs.owi.nldi.controllers.BaseController;
import gov.usgs.owi.nldi.model.Feature;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import mil.nga.sf.geojson.*;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.*;

public class FeatureMessageConverter extends AbstractHttpMessageConverter<Feature> {

  public FeatureMessageConverter(MediaType... supportedMediaTypes) {
    super(supportedMediaTypes);
  }

  @Override
  protected boolean supports(Class<?> aClass) {
    return Feature.class.isAssignableFrom(aClass);
  }

  @Override
  protected Feature readInternal(Class<? extends Feature> aClass, HttpInputMessage httpInputMessage)
      throws IOException, HttpMessageNotReadableException {
    return null;
  }

  @Override
  protected void writeInternal(Feature feature, HttpOutputMessage httpOutputMessage)
      throws IOException, HttpMessageNotWritableException {
    String contentType = httpOutputMessage.getHeaders().getContentType().toString();
    switch (contentType) {
      case BaseController.MIME_TYPE_JSONLD:
        writeJsonLd(feature, httpOutputMessage);
        break;
      case BaseController.MIME_TYPE_GEOJSON:
      case MediaType.APPLICATION_JSON_VALUE:
        writeGeoJson(feature, httpOutputMessage);
        break;
    }
  }

  private void writeJsonLd(Feature feature, HttpOutputMessage httpOutputMessage)
      throws IOException {
    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(httpOutputMessage.getBody());
    JsonFactoryBuilder builder = new JsonFactoryBuilder();

    try (JsonGenerator generator = builder.build().createGenerator(outputStreamWriter)) {
      // start final object vvv
      generator.writeStartObject();

      // context array vvv
      generator.writeArrayFieldStart("@context");
      generator.writeStartObject();

      generator.writeStringField("schema", "https://schema.org/");
      generator.writeStringField("geo", "schema:geo");
      generator.writeStringField("hyf", "https://www.opengis.net/def/schema/hy_features/hyf/");
      generator.writeStringField("gsp", "http://www.opengis.net/ont/geosparql#");
      generator.writeStringField("name", "schema:name");
      generator.writeStringField("source", "schema:provider");

      generator.writeObjectFieldStart("comid");
      generator.writeStringField("@id", "schema:geoWithin");
      generator.writeStringField("@type", "@id");
      generator.writeEndObject();

      generator.writeObjectFieldStart("mainstem");
      generator.writeStringField("@id", "hyf:linearElement");
      generator.writeStringField("@type", "@id");
      generator.writeEndObject();

      generator.writeEndObject();
      generator.writeEndArray();
      // context array ^^^

      generator.writeStringField("@id", feature.getUri());
      generator.writeStringField(
          "@type", "https://www.opengis.net/def/schema/hy_features/hyf/HY_HydroLocation");

      // source object vvv
      generator.writeObjectFieldStart("source");
      generator.writeStringField("@type", "schema:Organization");
      generator.writeStringField("schema:name", feature.getSource());
      generator.writeStringField("schema:description", feature.getSourceName());
      generator.writeEndObject();
      // source object ^^^

      generator.writeStringField("name", feature.getName());
      generator.writeStringField(
          "comid", "https://geoconnex.us/nhdplusv2/comid/" + feature.getComid());

      // hyf referenced position vvv
      generator.writeArrayFieldStart("hyf:referencedPosition");

      if (!feature.getMainstemUri().equalsIgnoreCase("NA")) {
        generator.writeStartObject();
        generator.writeObjectFieldStart("hyf:HY_IndirectPosition");
        generator.writeStringField("mainstem", feature.getMainstemUri());
        generator.writeEndObject();
        generator.writeEndObject();
      }

      if (feature.getMeasure() != null && feature.getReachcode() != null) {
        generator.writeStartObject();
        generator.writeObjectFieldStart("hyf:HY_IndirectPosition");

        generator.writeObjectFieldStart("hyf:distanceExpression");
        generator.writeObjectFieldStart("hyf:HY_DistanceFromReferent");
        generator.writeStringField("hyf:interpolative", String.valueOf(feature.getMeasure()));
        generator.writeEndObject();
        generator.writeEndObject();

        generator.writeObjectFieldStart("hyf:distanceDescription");
        generator.writeStringField("hyf:HY_DistanceDescription", "upstream");
        generator.writeEndObject();

        generator.writeStringField(
            "hyf:linearElement",
            "https://geoconnex.us/nhdplusv2/reachcode/" + feature.getReachcode());

        generator.writeEndObject();
        generator.writeEndObject();
      }

      generator.writeEndArray();
      // hyf referenced position ^^^

      if (feature.getGeometry().getGeometryType() == GeometryType.POINT) {
        Point point = (Point) feature.getGeometry();
        Position position = point.getCoordinates();

        generator.writeObjectFieldStart("geo");
        generator.writeStringField("@type", "schema:GeoCoordinates");
        generator.writeNumberField("schema:longitude", position.getX());
        generator.writeNumberField("schema:latitude", position.getY());
        generator.writeEndObject();
      }

      generator.writeObjectFieldStart("gsp:hasGeometry");
      generator.writeStringField(
          "@type", "http://www.opengis.net/ont/sf#" + feature.getGeometry().getType());
      generator.writeObjectFieldStart("gsp:asWKT");
      generator.writeStringField("@value", feature.getWellKnownText());
      generator.writeStringField("@type", "http://www.opengis.net/ont/geosparql#wktLiteral");
      generator.writeEndObject();
      generator.writeEndObject();

      generator.writeEndObject();
      // end final object ^^^
    }
  }

  private void writeGeoJson(Feature feature, HttpOutputMessage httpOutputMessage)
      throws IOException {

    mil.nga.sf.geojson.Feature geoJsonFeature = new mil.nga.sf.geojson.Feature();
    geoJsonFeature.setGeometry(feature.getGeometry());

    Map<String, Object> properties = new HashMap<>();
    properties.put("type", feature.getType());
    properties.put("source", feature.getSource());
    properties.put("sourceName", feature.getSourceName());
    properties.put("identifier", feature.getIdentifier());
    properties.put("name", feature.getName());
    properties.put("uri", feature.getUri());
    properties.put("comid", feature.getComid().toString());

    if (feature.getReachcode() != null) {
      properties.put("reachcode", feature.getReachcode());
    }

    if (feature.getMeasure() != null) {
      properties.put("measure", feature.getMeasure());
    }

    properties.put("navigation", feature.getNavigation());

    if (!feature.getMainstemUri().equals("NA")) {
      properties.put("mainstem", feature.getMainstemUri());
    }

    geoJsonFeature.setProperties(properties);

    FeatureCollection collection = new FeatureCollection(geoJsonFeature);
    String geoJsonString = FeatureConverter.toStringValue(collection);

    byte[] output = geoJsonString.getBytes(StandardCharsets.UTF_8);
    httpOutputMessage.getBody().write(output);
  }
}
