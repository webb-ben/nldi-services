package gov.usgs.owi.nldi.exceptions;

public class ComidNotFoundException extends RuntimeException {
  public ComidNotFoundException(Integer comid) {
    super(String.format("The comid \"%s\" does not exist.", comid));
  }

  public ComidNotFoundException(String featureSource, String featureID) {
    super(
        String.format(
            "The comid for feature source \"%s\" and feature ID \"%s\" does not exist.",
            featureSource, featureID));
  }
}
