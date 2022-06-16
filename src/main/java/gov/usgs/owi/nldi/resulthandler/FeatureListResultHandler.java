package gov.usgs.owi.nldi.resulthandler;

import gov.usgs.owi.nldi.model.Feature;
import gov.usgs.owi.nldi.model.FeatureList;
import org.apache.ibatis.session.ResultContext;
import org.apache.ibatis.session.ResultHandler;

public class FeatureListResultHandler implements ResultHandler<Feature> {

  private FeatureList featureList;

  public FeatureListResultHandler() {
    featureList = new FeatureList();
  }

  @Override
  public void handleResult(ResultContext<? extends Feature> resultContext) {
    featureList.addFeature(resultContext.getResultObject());
  }

  public FeatureList getFeatureList() {
    return featureList;
  }
}
