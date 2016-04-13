package gov.usgs.owi.nldi.controllers;

import gov.usgs.owi.nldi.dao.BaseDao;
import gov.usgs.owi.nldi.dao.CountDao;
import gov.usgs.owi.nldi.dao.StreamingDao;
import gov.usgs.owi.nldi.dao.StreamingResultHandler;
import gov.usgs.owi.nldi.services.Navigation;
import gov.usgs.owi.nldi.transform.FeatureTransformer;
import gov.usgs.owi.nldi.transform.FlowLineTransformer;
import gov.usgs.owi.nldi.transform.ITransformer;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.session.ResultHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping(value="/comid/{comid}/navigate/{navigationMode}")
public class RestController {
	private static final Logger LOG = LoggerFactory.getLogger(RestController.class);
	
	public static final String DATA_SOURCE = "dataSource";

	public static final String NAVIGATE = "navigate";
	public static final String SESSION_ID = "sessionId";
	public static final String COUNT_SUFFIX = "_count";
	
	public static final String HEADER_CONTENT_TYPE = "Content-Type";
	public static final String MIME_TYPE_GEOJSON = "application/vnd.geo+json";
    public static final String FEATURE_COUNT_HEADER = BaseDao.FEATURES + COUNT_SUFFIX;
    public static final String FLOW_LINES_COUNT_HEADER = BaseDao.FLOW_LINES + COUNT_SUFFIX;


	protected final CountDao countDao;
	protected final StreamingDao streamingDao;
	protected final Navigation navigation;

	@Autowired
	public RestController(CountDao inCountDao, StreamingDao inStreamingDao, Navigation inNavigation) {
		countDao = inCountDao;
		streamingDao = inStreamingDao;
		navigation = inNavigation;
	}

	@RequestMapping(method=RequestMethod.GET)
    public void getFlowlines(HttpServletRequest request, HttpServletResponse response,
    		@PathVariable(Navigation.COMID) String comid,
    		@PathVariable(Navigation.NAVIGATION_MODE) String navigationMode,
    		@RequestParam(value=Navigation.STOP_COMID, required=false) String stopComid,
    		@RequestParam(value=Navigation.DISTANCE, required=false) String distance) {
		OutputStream responseStream = null;

		try {
			responseStream = new BufferedOutputStream(response.getOutputStream());
			String sessionId = navigation.navigate(responseStream, comid, navigationMode, distance, stopComid);
			if (null != sessionId) {
				Map<String, Object> parameterMap = new HashMap<> ();
				parameterMap.put(SESSION_ID, sessionId);
				addHeaders(response, BaseDao.FLOW_LINES, parameterMap);
				streamResults(new FlowLineTransformer(responseStream), BaseDao.FLOW_LINES, parameterMap);
			} else {
				response.setStatus(HttpStatus.BAD_REQUEST.value());
			}

		} catch (Exception e) {
			LOG.error("Handle me better" + e.getLocalizedMessage(), e);
		} finally {
			if (null != responseStream) {
				try {
					responseStream.flush();
				} catch (IOException e) {
					//Just log, cause we obviously can't tell the client
					LOG.error("Error flushing response stream", e);
				}
			}
		}
    }

	@RequestMapping(value="{dataSource}", method=RequestMethod.GET)
    public void getFeatures(HttpServletRequest request, HttpServletResponse response,
    		@PathVariable(Navigation.COMID) String comid,
    		@PathVariable(Navigation.NAVIGATION_MODE) String navigationMode,
    		@PathVariable(value=DATA_SOURCE) String dataSource,
    		@RequestParam(value=Navigation.STOP_COMID, required=false) String stopComid,
    		@RequestParam(value=Navigation.DISTANCE, required=false) String distance) {
		OutputStream responseStream = null;

		try {
			responseStream = new BufferedOutputStream(response.getOutputStream());
			String sessionId = navigation.navigate(responseStream, comid, navigationMode, distance, stopComid);
			if (null != sessionId) {
				Map<String, Object> parameterMap = new HashMap<> ();
				parameterMap.put(SESSION_ID, sessionId);
				parameterMap.put(DATA_SOURCE, dataSource.toLowerCase());
				addHeaders(response, BaseDao.FEATURES, parameterMap);
				streamResults(new FeatureTransformer(responseStream), BaseDao.FEATURES, parameterMap);
			} else {
				response.setStatus(HttpStatus.BAD_REQUEST.value());
			}

		} catch (Exception e) {
			LOG.error("Handle me better" + e.getLocalizedMessage(), e);
		} finally {
			if (null != responseStream) {
				try {
					responseStream.flush();
				} catch (IOException e) {
					//Just log, cause we obviously can't tell the client
					LOG.error("Error flushing response stream", e);
				}
			}
		}
    }

	protected void streamResults(ITransformer transformer, String featureType, Map<String, Object> parameterMap) {
		ResultHandler<?> handler = new StreamingResultHandler(transformer);
		streamingDao.stream(featureType, parameterMap, handler);
		transformer.end();		
	}

	protected void addHeaders(HttpServletResponse response, String featureType, Map<String, Object> parameterMap) {
		response.setHeader(HEADER_CONTENT_TYPE, MIME_TYPE_GEOJSON);
		response.setHeader(featureType + COUNT_SUFFIX, countDao.count(featureType, parameterMap));
	}

}
