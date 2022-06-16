package gov.usgs.owi.nldi.model;

import org.springframework.lang.NonNull;

import java.util.ArrayList;
import java.util.List;

public class FeatureList {

    private List<Feature> features;

    public FeatureList() {
        features = new ArrayList<>();
    }

    public void addFeature(Feature feature) {
        features.add(feature);
    }

    public List<Feature> getFeatures() {
        return features;
    }

    public void setNavigationUrls(@NonNull String linkedDataBaseUrl) {
        for (Feature feature: features) {
            feature.setNavigation(
                    String.join("/",
                            linkedDataBaseUrl,
                            feature.getSource().toLowerCase(),
                            feature.getIdentifier(),
                            "navigation"));
        }
    }
}
