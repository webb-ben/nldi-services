package gov.usgs.owi.nldi.controllers;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import gov.usgs.owi.nldi.dao.BaseDao;
import gov.usgs.owi.nldi.dao.LookupDao;
import gov.usgs.owi.nldi.dao.NavigationDao;
import gov.usgs.owi.nldi.services.Navigation;
import gov.usgs.owi.nldi.transform.FeatureTransformer;
import gov.usgs.owi.nldi.transform.MapToGeoJsonTransformer;

@Controller
public class LookupController {

	public static final String ROOT_URL = "rootUrl";

	protected final LookupDao lookupDao;
	protected final String rootUrl;

	@Autowired
	public LookupController(LookupDao inLookupDao,
			@Qualifier("rootUrl") String inRootUrl) {
		this.lookupDao = inLookupDao;
		this.rootUrl = inRootUrl;
	}

	@RequestMapping(method=RequestMethod.GET, value="/", produces=MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public List<Map<String, Object>> getDataSources() {
		List<Map<String, Object>> rtn = new ArrayList<>();
		Map<String, Object> comid = new LinkedHashMap<>();
		comid.put(FeatureTransformer.SOURCE, FeatureTransformer.COMID);
		comid.put(FeatureTransformer.SOURCE_NAME, "NHDPlus comid");
		comid.put(BaseDao.FEATURES, String.join("/", rootUrl, FeatureTransformer.COMID));
		rtn.add(comid);

		Map<String, Object> parameterMap = new HashMap<>();
		parameterMap.put(ROOT_URL, rootUrl);
		rtn.addAll(lookupDao.getList(BaseDao.DATA_SOURCES, parameterMap));

		return rtn;
	}

	@RequestMapping(method=RequestMethod.GET, value="/{featureSource}/{featureID}/navigate", produces=MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Map<String, Object> getNavigationTypes(HttpServletRequest request, HttpServletResponse response,
			@PathVariable(LinkedDataController.FEATURE_SOURCE) String featureSource,
			@PathVariable(LinkedDataController.FEATURE_ID) String featureID) throws UnsupportedEncodingException {
		Map<String, Object> rtn = new LinkedHashMap<>();

		//Verify that the source and identifier are valid
		Map<String, Object> parameterMap = new HashMap<>();
		parameterMap.put(LinkedDataController.FEATURE_SOURCE, featureSource);
		parameterMap.put(LinkedDataController.FEATURE_ID, featureID);
		Map<String, Object> results = lookupDao.getOne(BaseDao.FEATURE, parameterMap);

		if (null == results || results.isEmpty()) {
			response.setStatus(HttpStatus.NOT_FOUND.value());
		} else {
			rtn.put(Navigation.UPSTREAM_MAIN, 
					String.join("/", rootUrl, featureSource.toLowerCase(), URLEncoder.encode(featureID, MapToGeoJsonTransformer.DEFAULT_ENCODING), NavigationDao.NAVIGATE, Navigation.UM));
			rtn.put(Navigation.UPSTREAM_TRIBUTARIES, 
					String.join("/", rootUrl, featureSource.toLowerCase(), URLEncoder.encode(featureID, MapToGeoJsonTransformer.DEFAULT_ENCODING), NavigationDao.NAVIGATE, Navigation.UT));
			rtn.put(Navigation.DOWNSTREAM_MAIN, 
					String.join("/", rootUrl, featureSource.toLowerCase(), URLEncoder.encode(featureID, MapToGeoJsonTransformer.DEFAULT_ENCODING), NavigationDao.NAVIGATE, Navigation.DM));
			rtn.put(Navigation.DOWNSTREAM_DIVERSIONS, 
					String.join("/", rootUrl, featureSource.toLowerCase(), URLEncoder.encode(featureID, MapToGeoJsonTransformer.DEFAULT_ENCODING), NavigationDao.NAVIGATE, Navigation.DD));
		}

		return rtn;
	}

}
