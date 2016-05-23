package gov.usgs.owi.nldi.controllers;

import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import gov.usgs.owi.nldi.dao.BaseDao;
import gov.usgs.owi.nldi.dao.CountDao;
import gov.usgs.owi.nldi.dao.LookupDao;
import gov.usgs.owi.nldi.dao.StreamingDao;
import gov.usgs.owi.nldi.services.Navigation;
import gov.usgs.owi.nldi.transform.FeatureTransformer;

@Controller
@RequestMapping(value="/{featureSource}/{featureID}")
public class LinkedDataController extends BaseController {
	private static final Logger LOG = LoggerFactory.getLogger(LinkedDataController.class);

	public static final String FEATURE_SOURCE = "featureSource";
	public static final String FEATURE_ID = "featureID";
	protected final LookupDao lookupDao;

	@Autowired
	public LinkedDataController(CountDao inCountDao, StreamingDao inStreamingDao, Navigation inNavigation,
			LookupDao inLookupDao, @Qualifier("rootUrl") String inRootUrl) {
		super(inCountDao, inStreamingDao, inNavigation, inRootUrl);
		this.lookupDao = inLookupDao;
	}

	@RequestMapping(method=RequestMethod.GET)
	public void getRegisteredPoint(HttpServletRequest request, HttpServletResponse response,
			@PathVariable(FEATURE_SOURCE) String featureSource,
			@PathVariable(FEATURE_ID) String featureID) {
		OutputStream responseStream = null;

		try {
			responseStream = new BufferedOutputStream(response.getOutputStream());
			Map<String, Object> parameterMap = new HashMap<> ();
			parameterMap.put(FEATURE_SOURCE, featureSource);
			parameterMap.put(FEATURE_ID, featureID);
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

	@RequestMapping(value="/navigate/{navigationMode}", method=RequestMethod.GET)
	public void getFlowlines(HttpServletRequest request, HttpServletResponse response,
			@PathVariable(FEATURE_SOURCE) String featureSource,
			@PathVariable(FEATURE_ID) String featureID,
			@PathVariable(Navigation.NAVIGATION_MODE) String navigationMode,
			@RequestParam(value=Navigation.STOP_COMID, required=false) String stopComid,
			@RequestParam(value=Navigation.DISTANCE, required=false) String distance) {

		streamFlowLines(response, getComid(featureSource, featureID), navigationMode, stopComid, distance);
	}

	@RequestMapping(value="/navigate/{navigationMode}/{dataSource}", method=RequestMethod.GET)
	public void getFeatures(HttpServletRequest request, HttpServletResponse response,
			@PathVariable(FEATURE_SOURCE) String featureSource,
			@PathVariable(FEATURE_ID) String featureID,
			@PathVariable(Navigation.NAVIGATION_MODE) String navigationMode,
			@PathVariable(value=DATA_SOURCE) String dataSource,
			@RequestParam(value=Navigation.STOP_COMID, required=false) String stopComid,
			@RequestParam(value=Navigation.DISTANCE, required=false) String distance) {

		streamFeatures(response, getComid(featureSource, featureID), navigationMode, stopComid, distance, dataSource);
	}

	protected String getComid(String featureSource, String featureID) {
		Map<String, Object> parameterMap = new HashMap<> ();
		parameterMap.put(FEATURE_SOURCE, featureSource);
		parameterMap.put(FEATURE_ID, featureID);

		Map<String, Object> feature = lookupDao.getOne(BaseDao.FEATURE, parameterMap);
		return feature.get(Navigation.COMID).toString();
	}

}
