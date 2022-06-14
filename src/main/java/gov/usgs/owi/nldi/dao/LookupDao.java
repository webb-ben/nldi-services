package gov.usgs.owi.nldi.dao;

import gov.usgs.owi.nldi.exceptions.DataSourceNotFoundException;
import gov.usgs.owi.nldi.exceptions.FeatureIdNotFoundException;
import gov.usgs.owi.nldi.exceptions.FeatureSourceNotFoundException;
import gov.usgs.owi.nldi.model.Comid;
import gov.usgs.owi.nldi.model.DataSource;
import gov.usgs.owi.nldi.model.Feature;
import gov.usgs.owi.nldi.services.Parameters;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mil.nga.sf.geojson.Position;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class LookupDao extends BaseDao {

  public static final String ROOT_URL = "rootUrl";
  public static final String NAME = "name";
  public static final String URI = "uri";
  public static final String SHAPE = "shape";
  public static final String IDENTIFIER = "identifier";
  public static final String FEATURE_SOURCE = "featureSource";
  public static final String SOURCE = "source";
  public static final String SOURCE_NAME = "sourceName";
  public static final String COMID_LAT_LON = "comidLatLon";
  public static final String ST_DISTANCE = "distanceFromFlowline";
  public static final String ST_CLOSEST_POINT = "closestPointOnFlowline";
  public static final String FEATURE_LOCATION = "featureLocation";
  public static final String GET_MEASURE = "getMeasure";
  public static final String MEASURE_ESTIMATE = "measureEstimate";
  public static final String REACH_CODE = "reachCode";

  private static final String NS = "lookup.";

  @Autowired
  public LookupDao(SqlSessionFactory sqlSessionFactory) {
    super(sqlSessionFactory);
  }

  public void validateDataSource(String dataSource) throws DataSourceNotFoundException {
    Map<String, Object> parameterMap = new HashMap<>();
    parameterMap.put(LookupDao.FEATURE_SOURCE, dataSource);

    Map<String, Object> feature = getSqlSession().selectOne(NS + FEATURE_SOURCE, parameterMap);
    if (null == feature) {
      throw new DataSourceNotFoundException(dataSource);
    }
  }

  public void validateFeatureSource(String featureSource) throws FeatureSourceNotFoundException {
    if (featureSource.equals(COMID)) {
      return;
    }

    Map<String, Object> parameterMap = new HashMap<>();
    parameterMap.put(LookupDao.FEATURE_SOURCE, featureSource);

    Map<String, Object> feature = getSqlSession().selectOne(NS + FEATURE_SOURCE, parameterMap);
    if (null == feature) {
      throw new FeatureSourceNotFoundException(featureSource);
    }
  }

  public void validateFeatureSourceAndId(String featureSource, String featureID)
      throws FeatureIdNotFoundException {
    if (featureSource.equals(COMID)) {
      return;
    }

    Map<String, Object> parameterMap = new HashMap<>();
    parameterMap.put(LookupDao.FEATURE_SOURCE, featureSource);
    parameterMap.put(Parameters.FEATURE_ID, featureID);

    Map<String, Object> feature = getSqlSession().selectOne(NS + BaseDao.FEATURE, parameterMap);
    if (null == feature) {
      throw new FeatureIdNotFoundException(featureSource, featureID);
    }
  }

  public List<DataSource> getDataSources(@NonNull String linkedDataUrl) {
    Map<String, Object> parameterMap = new HashMap<>();
    parameterMap.put(ROOT_URL, linkedDataUrl);

    return getSqlSession().selectList(NS + BaseDao.DATA_SOURCES, parameterMap);
  }

  public Feature getFeature(@NonNull String featureSource, @NonNull String featureID) {
    Map<String, Object> parameterMap = new HashMap<>();
    parameterMap.put(LookupDao.FEATURE_SOURCE, featureSource);
    parameterMap.put(Parameters.FEATURE_ID, featureID);

    Feature result = null;
    result = getSqlSession().selectOne(NS + "fancyFeature", parameterMap);
    return result;
  }

  public Comid getComid(@NonNull Integer comid) {
    Comid result = null;
    result = getSqlSession().selectOne(NS + "fancyComid", comid);
    return result;
  }

  public Integer getFeatureComid(String featureSource, String featureID)
      throws IllegalArgumentException {
    if (null == featureSource || null == featureID) {
      throw new IllegalArgumentException("A featureSource and featureID are required.");
    }

    Map<String, Object> parameterMap = new HashMap<>();
    parameterMap.put(LookupDao.FEATURE_SOURCE, featureSource);
    parameterMap.put(Parameters.FEATURE_ID, featureID);

    Map<String, Object> feature = getSqlSession().selectOne(NS + BaseDao.FEATURE, parameterMap);
    if (null == feature || !feature.containsKey(Parameters.COMID)) {
      return null;
    } else {
      return Integer.parseInt(feature.get(Parameters.COMID).toString());
    }
  }

  public String getMeasure(String featureSource, String featureID, Boolean allowEstimate) {
    Map<String, Object> parameterMap = new HashMap<>();
    parameterMap.put(LookupDao.FEATURE_SOURCE, featureSource);
    parameterMap.put(Parameters.FEATURE_ID, featureID);
    parameterMap.put(GET_MEASURE, true);

    Map<String, Object> feature = getSqlSession().selectOne(NS + BaseDao.FEATURE, parameterMap);

    // get measure estimate if feature does not have explicit measure
    if (allowEstimate && null != feature && null == feature.get(Parameters.MEASURE)) {
      parameterMap.remove(LookupDao.GET_MEASURE);
      feature.clear();

      feature = getSqlSession().selectOne(NS + MEASURE_ESTIMATE, parameterMap);
    }

    if (null == feature || !feature.containsKey(Parameters.MEASURE)) {
      return null;
    } else {
      return feature.get(Parameters.MEASURE).toString();
    }
  }

  public String getMeasure(Integer comid, Position position) {
    Map<String, Object> parameterMap = new HashMap<>();
    parameterMap.put(Parameters.COMID, comid);
    parameterMap.put(Parameters.LATITUDE, position.getY());
    parameterMap.put(Parameters.LONGITUDE, position.getX());

    Map<String, Object> feature = getSqlSession().selectOne(NS + MEASURE_ESTIMATE, parameterMap);

    if (null == feature || !feature.containsKey(Parameters.MEASURE)) {
      return null;
    } else {
      return feature.get(Parameters.MEASURE).toString();
    }
  }

  public String getReachCode(Integer comid) {
    Map<String, Object> parameterMap = new HashMap<>();
    parameterMap.put(Parameters.COMID, comid);

    return getSqlSession().selectOne(NS + REACH_CODE, parameterMap);
  }

  public Integer getComidByLatitudeAndLongitude(Position position) {
    Map<String, Object> parameterMap = new HashMap<>();
    parameterMap.put(Parameters.LONGITUDE, position.getX());
    parameterMap.put(Parameters.LATITUDE, position.getY());
    return getSqlSession().selectOne(NS + COMID_LAT_LON, parameterMap);
  }

  public List<Map<String, Object>> getList(String objectType, Map<String, Object> parameterMap) {
    return getSqlSession().selectList(NS + objectType, parameterMap);
  }

  public float getDistanceFromFlowline(String featureSource, String featureID) {
    if (null == featureSource || null == featureID) {
      throw new IllegalArgumentException("A featureSource and featureID are required");
    }

    Map<String, Object> parameterMap = new HashMap<>();
    parameterMap.put(LookupDao.FEATURE_SOURCE, featureSource);
    parameterMap.put(Parameters.FEATURE_ID, featureID);
    return getSqlSession().selectOne(NS + ST_DISTANCE, parameterMap);
  }

  public Position getClosestPointOnFlowline(String featureSource, String featureID)
      throws Exception {
    if (null == featureSource || null == featureID) {
      throw new IllegalArgumentException("A featureSource and featureID are required");
    }

    Map<String, Object> parameterMap = new HashMap<>();
    parameterMap.put(LookupDao.FEATURE_SOURCE, featureSource);
    parameterMap.put(Parameters.FEATURE_ID, featureID);

    Map<String, Double> result = getSqlSession().selectOne(NS + ST_CLOSEST_POINT, parameterMap);

    if (!result.containsKey("lat") || !result.containsKey("lon")) {
      throw new Exception("getClosestPointOnFlowline did not return lat or lon");
    }

    Position position =
        new Position(result.get(Parameters.LONGITUDE), result.get(Parameters.LATITUDE));

    return position;
  }

  public Position getFeatureLocation(String featureSource, String featureID) throws Exception {

    if (null == featureSource || null == featureID) {
      throw new IllegalArgumentException("A featureSource and featureID are required");
    }

    Map<String, Object> parameterMap = new HashMap<>();
    parameterMap.put(LookupDao.FEATURE_SOURCE, featureSource);
    parameterMap.put(Parameters.FEATURE_ID, featureID);

    Map<String, Object> result = getSqlSession().selectOne(NS + FEATURE_LOCATION, parameterMap);

    if (!result.containsKey("lat") || !result.containsKey("lon")) {
      throw new Exception("getFeatureLocation did not return lat or lon");
    }

    Position position =
        new Position(
            Double.parseDouble(result.get(Parameters.LONGITUDE).toString()),
            Double.parseDouble(result.get(Parameters.LATITUDE).toString()));

    return position;
  }
}
