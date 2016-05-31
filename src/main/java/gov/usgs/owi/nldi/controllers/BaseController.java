package gov.usgs.owi.nldi.controllers;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.session.ResultHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;

import de.jkeylockmanager.manager.KeyLockManager;
import de.jkeylockmanager.manager.KeyLockManagers;
import gov.usgs.owi.nldi.dao.BaseDao;
import gov.usgs.owi.nldi.dao.CountDao;
import gov.usgs.owi.nldi.dao.LookupDao;
import gov.usgs.owi.nldi.dao.NavigationDao;
import gov.usgs.owi.nldi.dao.StreamingDao;
import gov.usgs.owi.nldi.dao.StreamingResultHandler;
import gov.usgs.owi.nldi.services.Navigation;
import gov.usgs.owi.nldi.transform.FeatureTransformer;
import gov.usgs.owi.nldi.transform.FlowLineTransformer;
import gov.usgs.owi.nldi.transform.ITransformer;

public abstract class BaseController {
	private static final Logger LOG = LoggerFactory.getLogger(BaseController.class);

	public static final String DATA_SOURCE = "dataSource";

	public static final String NAVIGATE = NavigationDao.NAVIGATE;
	public static final String SESSION_ID = "sessionId";
	public static final String COUNT_SUFFIX = "_count";

	public static final String HEADER_CONTENT_TYPE = "Content-Type";
	public static final String MIME_TYPE_GEOJSON = "application/vnd.geo+json";
	public static final String FEATURE_COUNT_HEADER = BaseDao.FEATURES + COUNT_SUFFIX;
	public static final String FLOW_LINES_COUNT_HEADER = BaseDao.FLOW_LINES + COUNT_SUFFIX;


	protected final CountDao countDao;
	protected final LookupDao lookupDao;
	protected final StreamingDao streamingDao;
	protected final Navigation navigation;
	protected final String rootUrl;

	private final KeyLockManager lockManager = KeyLockManagers.newLock();

	public BaseController(CountDao inCountDao, LookupDao inLookupDao, StreamingDao inStreamingDao, Navigation inNavigation, String inRootUrl) {
		countDao = inCountDao;
		lookupDao = inLookupDao;
		streamingDao = inStreamingDao;
		navigation = inNavigation;
		rootUrl = inRootUrl;
	}

	@ExceptionHandler(Exception.class)
	public @ResponseBody String handleUncaughtException(Exception ex, WebRequest request, HttpServletResponse response) throws IOException {
		if (ex instanceof AccessDeniedException) {
			response.setStatus(HttpStatus.FORBIDDEN.value());
			return "You are not authorized to perform this action.";
		} else if (ex instanceof MissingServletRequestParameterException
				|| ex instanceof HttpMediaTypeNotSupportedException) {
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			return ex.getLocalizedMessage();
		} else if (ex instanceof HttpMessageNotReadableException) {
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			if (ex.getLocalizedMessage().contains("\n")) {
				//This exception's message contains implementation details after the new line, so only take up to that.
				return ex.getLocalizedMessage().substring(0, ex.getLocalizedMessage().indexOf("\n"));
			} else {
				return ex.getLocalizedMessage().replaceAll("([a-zA-Z]+\\.)+","");
			}
		} else {
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			int hashValue = response.hashCode();
			//Note: we are giving the user a generic message.  
			//Server logs can be used to troubleshoot problems.
			String msgText = "Something bad happened. Contact us with Reference Number: " + hashValue;
			LOG.error(msgText, ex);
			return msgText;
		}
	}

	protected void streamFlowLines(HttpServletResponse response,
			String comid, String navigationMode, String stopComid, String distance) {

		OutputStream responseStream = null;
		try {
			responseStream = new BufferedOutputStream(response.getOutputStream());
			String sessionId = getSessionId(responseStream, comid, navigationMode, distance, stopComid);
			if (null != sessionId) {
				Map<String, Object> parameterMap = new HashMap<> ();
				parameterMap.put(SESSION_ID, sessionId);
				addHeaders(response, BaseDao.FLOW_LINES, parameterMap);
				streamResults(new FlowLineTransformer(responseStream, rootUrl), BaseDao.FLOW_LINES, parameterMap);
			} else {
				response.setStatus(HttpStatus.BAD_REQUEST.value());
			}

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

	protected void streamFeatures(HttpServletResponse response,
			String comid, String navigationMode, String stopComid, String distance, String dataSource) {

		OutputStream responseStream = null;
		try {
			responseStream = new BufferedOutputStream(response.getOutputStream());
			String sessionId = getSessionId(responseStream, comid, navigationMode, distance, stopComid);
			if (null != sessionId) {
				Map<String, Object> parameterMap = new HashMap<> ();
				parameterMap.put(SESSION_ID, sessionId);
				parameterMap.put(DATA_SOURCE, dataSource.toLowerCase());
				addHeaders(response, BaseDao.FEATURES, parameterMap);
				streamResults(new FeatureTransformer(responseStream, rootUrl), BaseDao.FEATURES, parameterMap);
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
		LOG.trace("start streaming");
		ResultHandler<?> handler = new StreamingResultHandler(transformer);
		streamingDao.stream(featureType, parameterMap, handler);
		transformer.end();
		LOG.trace("done streaming");
	}

	protected void addContentHeader(HttpServletResponse response) {
		response.setHeader(HEADER_CONTENT_TYPE, MIME_TYPE_GEOJSON);
	}

	protected void addHeaders(HttpServletResponse response, String featureType, Map<String, Object> parameterMap) {
		LOG.trace("entering addHeaders");

		addContentHeader(response);
		response.setHeader(featureType + COUNT_SUFFIX, countDao.count(featureType, parameterMap));

		LOG.trace("leaving addHeaders");
	}

	protected String getSessionId(OutputStream responseStream, final String comid, final String navigationMode,
			final String distance, final String stopComid) {
		String key = String.join("|", comid, navigationMode, distance, stopComid);
		return lockManager.executeLocked(key, () -> navigation.navigate(responseStream, comid, navigationMode, distance, stopComid));
	}

}
