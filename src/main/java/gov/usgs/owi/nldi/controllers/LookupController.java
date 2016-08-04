package gov.usgs.owi.nldi.controllers;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gov.usgs.owi.nldi.dao.BaseDao;
import gov.usgs.owi.nldi.dao.CountDao;
import gov.usgs.owi.nldi.dao.LookupDao;
import gov.usgs.owi.nldi.dao.NavigationDao;
import gov.usgs.owi.nldi.dao.StreamingDao;
import gov.usgs.owi.nldi.services.Navigation;
import gov.usgs.owi.nldi.services.Parameters;
import gov.usgs.owi.nldi.transform.FeatureTransformer;
import gov.usgs.owi.nldi.transform.MapToGeoJsonTransformer;

@RestController
@RequestMapping(produces=MediaType.APPLICATION_JSON_VALUE)
public class LookupController extends BaseController {
	private static final Logger LOG = LoggerFactory.getLogger(LookupController.class);

	public static final String ROOT_URL = "rootUrl";

	@Autowired
	public LookupController(CountDao inCountDao, LookupDao inLookupDao, StreamingDao inStreamingDao,
			Navigation inNavigation, Parameters inParameters, @Qualifier("rootUrl") String inRootUrl) {
		super(inCountDao, inLookupDao, inStreamingDao, inNavigation, inParameters, inRootUrl);
	}

	@GetMapping(value="/")
	public List<Map<String, Object>> getDataSources() {
		List<Map<String, Object>> rtn = new ArrayList<>();
		Map<String, Object> featureSource = new LinkedHashMap<>();

		//Manually add comid as a feature source.
		featureSource.put(FeatureTransformer.SOURCE, FeatureTransformer.COMID);
		featureSource.put(FeatureTransformer.SOURCE_NAME, "NHDPlus comid");
		featureSource.put(BaseDao.FEATURES, String.join("/", rootUrl, FeatureTransformer.COMID));
		rtn.add(featureSource);

		Map<String, Object> parameterMap = new HashMap<>();
		parameterMap.put(ROOT_URL, rootUrl);
		rtn.addAll(lookupDao.getList(BaseDao.DATA_SOURCES, parameterMap));

		return rtn;
	}

	@GetMapping(value="/{featureSource}")
	public Object getFeatures(HttpServletResponse response) throws IOException {
		response.sendError(HttpStatus.BAD_REQUEST.value(), "This functionality is not implemented.");
		return null;
	}

	@GetMapping(value="/{featureSource}/{featureID}")
	public void getRegisteredFeature(HttpServletRequest request, HttpServletResponse response,
			@PathVariable(Parameters.FEATURE_SOURCE) String featureSource,
			@PathVariable(Parameters.FEATURE_ID) String featureID) {
		OutputStream responseStream = null;

		try {
			responseStream = new BufferedOutputStream(response.getOutputStream());
			Map<String, Object> parameterMap = new HashMap<> ();
			parameterMap.put(Parameters.FEATURE_SOURCE, featureSource);
			parameterMap.put(Parameters.FEATURE_ID, featureID);
			addContentHeader(response);
			streamResults(new FeatureTransformer(responseStream, rootUrl), BaseDao.FEATURE, parameterMap);

		} catch (Throwable e) {
			LOG.error("Handle me better" + e.getLocalizedMessage(), e);
		} finally {
			if (null != responseStream) {
				try {
					responseStream.flush();
				} catch (Throwable e) {
					//Just log, cause we obviously can't tell the client
					LOG.error("Error flushing response stream", e);
				}
			}
		}
	}

	@GetMapping(value="/{featureSource}/{featureID}/navigate")
	public Map<String, Object> getNavigationTypes(HttpServletRequest request, HttpServletResponse response,
			@PathVariable(Parameters.FEATURE_SOURCE) String featureSource,
			@PathVariable(Parameters.FEATURE_ID) String featureID) throws UnsupportedEncodingException {
		Map<String, Object> rtn = new LinkedHashMap<>();

		//Verify that the feature source and identifier are valid
		Map<String, Object> parameterMap = new HashMap<>();
		parameterMap.put(Parameters.FEATURE_SOURCE, featureSource);
		parameterMap.put(Parameters.FEATURE_ID, featureID);
		List<Map<String, Object>> results = lookupDao.getList(BaseDao.FEATURE, parameterMap);

		if (null == results || results.isEmpty()) {
			response.setStatus(HttpStatus.NOT_FOUND.value());
		} else {
			rtn.put(Parameters.UPSTREAM_MAIN, 
					String.join("/", rootUrl, featureSource.toLowerCase(), URLEncoder.encode(featureID, MapToGeoJsonTransformer.DEFAULT_ENCODING), NavigationDao.NAVIGATE, Parameters.UM));
			rtn.put(Parameters.UPSTREAM_TRIBUTARIES, 
					String.join("/", rootUrl, featureSource.toLowerCase(), URLEncoder.encode(featureID, MapToGeoJsonTransformer.DEFAULT_ENCODING), NavigationDao.NAVIGATE, Parameters.UT));
			rtn.put(Parameters.DOWNSTREAM_MAIN, 
					String.join("/", rootUrl, featureSource.toLowerCase(), URLEncoder.encode(featureID, MapToGeoJsonTransformer.DEFAULT_ENCODING), NavigationDao.NAVIGATE, Parameters.DM));
			rtn.put(Parameters.DOWNSTREAM_DIVERSIONS, 
					String.join("/", rootUrl, featureSource.toLowerCase(), URLEncoder.encode(featureID, MapToGeoJsonTransformer.DEFAULT_ENCODING), NavigationDao.NAVIGATE, Parameters.DD));
		}

		return rtn;
	}

}
