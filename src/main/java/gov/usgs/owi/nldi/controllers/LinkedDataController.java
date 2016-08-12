package gov.usgs.owi.nldi.controllers;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gov.usgs.owi.nldi.dao.BaseDao;
import gov.usgs.owi.nldi.dao.CountDao;
import gov.usgs.owi.nldi.dao.LookupDao;
import gov.usgs.owi.nldi.dao.StreamingDao;
import gov.usgs.owi.nldi.services.Navigation;
import gov.usgs.owi.nldi.services.Parameters;

@RestController
@RequestMapping(value="/{featureSource}/{featureID}/navigate/{navigationMode}", produces=MediaType.APPLICATION_JSON_VALUE)
public class LinkedDataController extends BaseController {

	@Autowired
	public LinkedDataController(CountDao inCountDao, LookupDao inLookupDao, StreamingDao inStreamingDao,
			Navigation inNavigation, Parameters inParameters, @Qualifier("rootUrl") String inRootUrl) {
		super(inCountDao, inLookupDao, inStreamingDao, inNavigation, inParameters, inRootUrl);
	}

	@GetMapping
	public void getFlowlines(HttpServletRequest request, HttpServletResponse response,
			@PathVariable(Parameters.FEATURE_SOURCE) String featureSource,
			@PathVariable(Parameters.FEATURE_ID) String featureID,
			@PathVariable(Parameters.NAVIGATION_MODE) String navigationMode,
			@RequestParam(value=Parameters.STOP_COMID, required=false) String stopComid,
			@RequestParam(value=Parameters.DISTANCE, required=false) String distance,
			@RequestParam(value=Parameters.LEGACY, required=false) String legacy) {

		String comid = getComid(featureSource, featureID);
		if (null == comid) {
			response.setStatus(HttpStatus.NOT_FOUND.value());
		} else {
			streamFlowLines(response, comid, navigationMode, stopComid, distance, isLegacy(legacy, navigationMode));
		}
	}

	@GetMapping(value="{dataSource}")
	public void getFeatures(HttpServletRequest request, HttpServletResponse response,
			@PathVariable(Parameters.FEATURE_SOURCE) String featureSource,
			@PathVariable(Parameters.FEATURE_ID) String featureID,
			@PathVariable(Parameters.NAVIGATION_MODE) String navigationMode,
			@PathVariable(value=DATA_SOURCE) String dataSource,
			@RequestParam(value=Parameters.STOP_COMID, required=false) String stopComid,
			@RequestParam(value=Parameters.DISTANCE, required=false) String distance,
			@RequestParam(value=Parameters.LEGACY, required=false) String legacy) {

		String comid = getComid(featureSource, featureID);
		if (null == comid) {
			response.setStatus(HttpStatus.NOT_FOUND.value());
		} else {
			streamFeatures(response, comid, navigationMode, stopComid, distance, dataSource, isLegacy(legacy, navigationMode));
		}
	}

	protected String getComid(String featureSource, String featureID) {
		Map<String, Object> parameterMap = new HashMap<> ();
		parameterMap.put(Parameters.FEATURE_SOURCE, featureSource);
		parameterMap.put(Parameters.FEATURE_ID, featureID);

		Map<String, Object> feature = lookupDao.getComid(BaseDao.FEATURE, parameterMap);
		if (null == feature || !feature.containsKey(Parameters.COMID)) {
			return null;
		} else {
			return feature.get(Parameters.COMID).toString();
		}
	}

}
