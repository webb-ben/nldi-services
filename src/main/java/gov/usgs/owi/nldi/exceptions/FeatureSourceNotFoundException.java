package gov.usgs.owi.nldi.exceptions;

public class FeatureSourceNotFoundException extends RuntimeException {
  public FeatureSourceNotFoundException(String featureSource) {
    super(String.format("The feature source \"%s\" does not exist.", featureSource));
  }
}
