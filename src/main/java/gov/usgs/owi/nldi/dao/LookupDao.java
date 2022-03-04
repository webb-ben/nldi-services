package gov.usgs.owi.nldi.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gov.usgs.owi.nldi.services.Parameters;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

	public Integer getFeatureComid(String featureSource, String featureID) throws IllegalArgumentException {
		if (null == featureSource|| null == featureID) {
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
		Map<String, Object> parameterMap = new HashMap<> ();
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

	public String getMeasure(Integer comid, String lat, String lon) {
		Map<String, Object> parameterMap = new HashMap<> ();
		parameterMap.put(Parameters.COMID, comid);
		parameterMap.put(Parameters.LATITUDE, lat);
		parameterMap.put(Parameters.LONGITUDE, lon);

		Map<String, Object> feature = getSqlSession().selectOne(NS + MEASURE_ESTIMATE, parameterMap);

		if (null == feature || !feature.containsKey(Parameters.MEASURE)) {
			return null;
		} else {
			return feature.get(Parameters.MEASURE).toString();
		}
	}

	public String getReachCode(Integer comid) {
		Map<String, Object> parameterMap = new HashMap<> ();
		parameterMap.put(Parameters.COMID, comid);

		return getSqlSession().selectOne(NS + REACH_CODE, parameterMap);
	}

	public Integer getComidByLatitudeAndLongitude(Map<String, Object> parameterMap) {
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

	public Map<String, Object> getClosestPointOnFlowline(String featureSource, String featureID) throws Exception {
		if (null == featureSource || null == featureID) {
			throw new IllegalArgumentException("A featureSource and featureID are required");
		}

		Map<String, Object> parameterMap = new HashMap<> ();
		parameterMap.put(LookupDao.FEATURE_SOURCE, featureSource);
		parameterMap.put(Parameters.FEATURE_ID, featureID);

		Map<String, Object> result = getSqlSession().selectOne(NS + ST_CLOSEST_POINT, parameterMap);

		if (!result.containsKey("lat") || !result.containsKey("lon")) {
			throw new Exception("getClosestPointOnFlowline did not return lat or lon");
		}

		return result;
	}

	public Map<String, Object> getFeatureLocation(String featureSource, String featureID) throws Exception {

		if (null == featureSource || null == featureID) {
			throw new IllegalArgumentException("A featureSource and featureID are required");
		}

		Map<String, Object> parameterMap = new HashMap<> ();
		parameterMap.put(LookupDao.FEATURE_SOURCE, featureSource);
		parameterMap.put(Parameters.FEATURE_ID, featureID);

		Map<String, Object> result = getSqlSession().selectOne(NS + FEATURE_LOCATION, parameterMap);

		if (!result.containsKey("lat") || !result.containsKey("lon")) {
			throw new Exception("getFeatureLocation did not return lat or lon");
		}

		return result;
	}

}
