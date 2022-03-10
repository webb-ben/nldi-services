package gov.usgs.owi.nldi.swagger.model;

import java.util.ArrayList;
import javax.validation.constraints.Size;

public class FeatureGeometry {
  public String type;

  @Size(min = 2, max = 2)
  public ArrayList<Double> coordinates;
}
