package gov.usgs.owi.nldi.converters;

import com.fasterxml.jackson.core.JsonFactoryBuilder;
import com.fasterxml.jackson.core.JsonGenerator;
import gov.usgs.owi.nldi.controllers.BaseController;
import gov.usgs.owi.nldi.model.Feature;
import gov.usgs.owi.nldi.model.FeatureList;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;
import mil.nga.sf.geojson.*;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

public class FeatureListMessageConverter extends AbstractHttpMessageConverter<FeatureList> {

  public FeatureListMessageConverter(MediaType... supportedMediaTypes) {
    super(supportedMediaTypes);
  }

  @Override
  protected boolean supports(Class<?> aClass) {
    return FeatureList.class.isAssignableFrom(aClass);
  }

  @Override
  protected FeatureList readInternal(
      Class<? extends FeatureList> aClass, HttpInputMessage httpInputMessage)
      throws IOException, HttpMessageNotReadableException {
    return null;
  }

  @Override
  protected void writeInternal(FeatureList featureList, HttpOutputMessage httpOutputMessage)
      throws IOException, HttpMessageNotWritableException {
    String contentType = httpOutputMessage.getHeaders().getContentType().toString();
    switch (contentType) {
      case BaseController.MIME_TYPE_JSONLD:
        writeJsonLd(featureList, httpOutputMessage);
        break;
      case BaseController.MIME_TYPE_GEOJSON:
      case MediaType.APPLICATION_JSON_VALUE:
        writeGeoJson(featureList, httpOutputMessage);
        break;
    }
  }

  private void writeJsonLd(FeatureList featureList, HttpOutputMessage httpOutputMessage)
      throws IOException {
    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(httpOutputMessage.getBody());
    JsonFactoryBuilder builder = new JsonFactoryBuilder();

    try (JsonGenerator generator = builder.build().createGenerator(outputStreamWriter)) {
      generator.writeStartObject();

      // context array vvv
      generator.writeArrayFieldStart("@context");
      generator.writeStartObject();

      generator.writeStringField("schema", "https://schema.org/");
      generator.writeStringField("geo", "schema:geo");
      generator.writeStringField("hyf", "https://www.opengis.net/def/schema/hy_features/hyf/");
      generator.writeStringField("gsp", "http://www.opengis.net/ont/geosparql#");
      generator.writeStringField("name", "schema:name");

      generator.writeObjectFieldStart("comid");
      generator.writeStringField("@id", "schema:geoWithin");
      generator.writeStringField("@type", "@id");
      generator.writeEndObject();

      generator.writeObjectFieldStart("hyf:linearElement");
      generator.writeStringField("@type", "@id");
      generator.writeEndObject();

      generator.writeEndObject();
      generator.writeEndArray();
      // context array ^^^

      generator.writeStringField("@id", "_:graph");
      generator.writeArrayFieldStart("@graph");

      for (Feature feature : featureList.getFeatures()) {
        // start final object vvv
        generator.writeStartObject();

        generator.writeStringField("@id", feature.getUri());
        generator.writeStringField(
            "@type", "https://www.opengis.net/def/schema/hy_features/hyf/HY_HydroLocation");

        // source object vvv
        generator.writeObjectFieldStart("schema:subjectOf");
        generator.writeStringField("@type", "schema:CreativeWork");
        generator.writeStringField("schema:identifier", feature.getSource());
        generator.writeStringField("schema:name", feature.getSourceName());
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
          generator.writeStringField("hyf:linearElement", feature.getMainstemUri());
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

      generator.writeEndArray();
      generator.writeEndObject();
    }
  }

  private void writeGeoJson(FeatureList featureList, HttpOutputMessage httpOutputMessage)
      throws IOException {

    FeatureCollection collection = new FeatureCollection();

    for (Feature feature : featureList.getFeatures()) {
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

      collection.addFeature(geoJsonFeature);
    }

    String geoJsonString = FeatureConverter.toStringValue(collection);

    byte[] output = geoJsonString.getBytes(StandardCharsets.UTF_8);
    httpOutputMessage.getBody().write(output);
  }
}
