package gov.usgs.owi.nldi.swagger.model;

import javax.validation.constraints.Size;
import java.util.ArrayList;

public class FeatureGeometry {
    public String type;
    @Size(min = 2, max = 2)
    public ArrayList<Double> coordinates;
}
