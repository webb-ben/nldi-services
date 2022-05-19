package gov.usgs.owi.nldi.exceptions;

public class FeatureIdNotFoundException extends RuntimeException {
  public FeatureIdNotFoundException(String featureSource, String featureId) {
    super(
        String.format(
            "The feature ID \"%s\" does not exist in feature source \"%s\".",
            featureId, featureSource));
  }
}
