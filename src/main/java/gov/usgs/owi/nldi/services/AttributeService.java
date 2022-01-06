package gov.usgs.owi.nldi.services;

import gov.usgs.owi.nldi.dao.BaseDao;
import gov.usgs.owi.nldi.dao.LookupDao;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AttributeService {
    private static final Logger LOG = LoggerFactory.getLogger(AttributeService.class);

    private final LookupDao lookupDao;

    @Autowired
    public AttributeService(LookupDao lookupDao) {
        this.lookupDao = lookupDao;
    }

    /**
     * Fetches a COM ID for non-null featureSource and featureID.
     * If either parameter is null, an IllegalArgumentException is thrown.
     *
     * @param featureSource
     * @param featureID
     * @return May return null if the COM ID is not found.
     */
    public String getComid(String featureSource, String featureID) {

        if (null == featureSource || null == featureID) {
            throw new IllegalArgumentException("A featureSource and featureID are required");
        }

        Map<String, Object> parameterMap = new HashMap<> ();
        parameterMap.put(LookupDao.FEATURE_SOURCE, featureSource);
        parameterMap.put(Parameters.FEATURE_ID, featureID);

        Map<String, Object> feature = lookupDao.getComid(BaseDao.FEATURE, parameterMap);
        if (null == feature || !feature.containsKey(Parameters.COMID)) {
            return null;
        } else {
            return feature.get(Parameters.COMID).toString();
        }
    }

    /**
     * Fetches a measure for non-null featureSource and featureID.
     * If either parameter is null, an IllegalArgumentException is thrown.
     *
     * @param featureSource
     * @param featureID
     * @return May return null if the measure is not found.
     */
    public String getMeasure(String featureSource, String featureID) {

        if (null == featureSource || null == featureID) {
            throw new IllegalArgumentException("A featureSource and featureID are required");
        }

        Map<String, Object> parameterMap = new HashMap<> ();
        parameterMap.put(LookupDao.FEATURE_SOURCE, featureSource);
        parameterMap.put(Parameters.FEATURE_ID, featureID);
        parameterMap.put(LookupDao.GET_MEASURE, true);

        Map<String, Object> feature = lookupDao.getMeasure(BaseDao.FEATURE, parameterMap);

        // get measure estimate if feature does not have explicit measure
        if (null != feature && null == feature.get(Parameters.MEASURE)) {
            parameterMap.remove(LookupDao.GET_MEASURE);
            feature.clear();

            feature = lookupDao.getMeasure(BaseDao.MEASURE_ESTIMATE, parameterMap);
        }

        if (null == feature || !feature.containsKey(Parameters.MEASURE)) {
            return null;
        } else {
            return feature.get(Parameters.MEASURE).toString();
        }
    }

    /**
     * Fetches distance to flowline for non-null featureSource and featureID.
     * If either parameter is null, an IllegalArgumentException is thrown.
     *
     * @param featureSource
     * @param featureID
     * @return Distance as a Double
     */
    public float getDistanceFromFlowline(String featureSource, String featureID) {

        if (null == featureSource || null == featureID) {
            throw new IllegalArgumentException("A featureSource and featureID are required");
        }

        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put(LookupDao.FEATURE_SOURCE, featureSource);
        parameterMap.put(Parameters.FEATURE_ID, featureID);

        return lookupDao.getDistanceFromFlowline(parameterMap);
    }

    /**
     * Fetches closest point on associated flowline for non-null featureSource and featureID.
     * If either parameter is null, an IllegalArgumentException is thrown.
     *
     * @param featureSource
     * @param featureID
     * @return Map with "lat" and "lon" keys indicating the closest point on flowline
     */
    public Map<String, Object> getClosestPointOnFlowline(String featureSource, String featureID) throws Exception {

        if (null == featureSource || null == featureID) {
            throw new IllegalArgumentException("A featureSource and featureID are required");
        }

        Map<String, Object> parameterMap = new HashMap<> ();
        parameterMap.put(LookupDao.FEATURE_SOURCE, featureSource);
        parameterMap.put(Parameters.FEATURE_ID, featureID);

        Map<String, Object> result = lookupDao.closestPointOnFlowline(parameterMap);

        if (!result.containsKey("lat") || !result.containsKey("lon")) {
            throw new Exception("getClosestPointOnFlowline did not return lat or lon");
        }

        return result;
    }

    /**
     * Fetches a feature's lat, lon location for non-null featureSource and featureID.
     * If either parameter is null, an IllegalArgumentException is thrown.
     *
     * @param featureSource
     * @param featureID
     * @return Map with "lat" and "lon" keys indicating the feature location
     */
    public Map<String, Object> getFeatureLocation(String featureSource, String featureID) throws Exception {

        if (null == featureSource || null == featureID) {
            throw new IllegalArgumentException("A featureSource and featureID are required");
        }

        Map<String, Object> parameterMap = new HashMap<> ();
        parameterMap.put(LookupDao.FEATURE_SOURCE, featureSource);
        parameterMap.put(Parameters.FEATURE_ID, featureID);

        Map<String, Object> result = lookupDao.getFeatureLocation(parameterMap);

        if (!result.containsKey("lat") || !result.containsKey("lon")) {
            throw new Exception("getFeatureLocation did not return lat or lon");
        }

        return result;
    }
}
