package gov.usgs.owi.nldi.controllers;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.session.ResultHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.util.NumberUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;

import de.jkeylockmanager.manager.KeyLockManager;
import de.jkeylockmanager.manager.KeyLockManagers;
import gov.usgs.owi.nldi.NavigationMode;
import gov.usgs.owi.nldi.dao.BaseDao;
import gov.usgs.owi.nldi.dao.LookupDao;
import gov.usgs.owi.nldi.dao.NavigationDao;
import gov.usgs.owi.nldi.dao.StreamingDao;
import gov.usgs.owi.nldi.dao.StreamingResultHandler;
import gov.usgs.owi.nldi.services.LogService;
import gov.usgs.owi.nldi.services.Navigation;
import gov.usgs.owi.nldi.services.Parameters;
import gov.usgs.owi.nldi.transform.FeatureTransformer;
import gov.usgs.owi.nldi.transform.FlowLineTransformer;
import gov.usgs.owi.nldi.transform.ITransformer;

public abstract class BaseController {
	private static final Logger LOG = LoggerFactory.getLogger(BaseController.class);

	public static final String DATA_SOURCE = "dataSource";

	public static final String NAVIGATE = NavigationDao.NAVIGATE;
	public static final String SESSION_ID = "sessionId";

	public static final String HEADER_CONTENT_TYPE = "Content-Type";
	public static final String MIME_TYPE_GEOJSON = "application/vnd.geo+json";

	protected final LookupDao lookupDao;
	protected final StreamingDao streamingDao;
	protected final Navigation navigation;
	protected final Parameters parameters;
	protected final String rootUrl;
	protected final LogService logService;

	private final KeyLockManager lockManager = KeyLockManagers.newLock();

	public BaseController(LookupDao inLookupDao, StreamingDao inStreamingDao, Navigation inNavigation, Parameters inParameters, String inRootUrl, LogService inLogService) {
		lookupDao = inLookupDao;
		streamingDao = inStreamingDao;
		navigation = inNavigation;
		parameters = inParameters;
		rootUrl = inRootUrl;
		logService = inLogService;
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
			String comid, String navigationMode, String stopComid, String distance, boolean legacy) throws IOException {
		Map<String, Object> parameterMap = parameters.processParameters(comid, navigationMode, distance, stopComid);
		try (FlowLineTransformer transformer = new FlowLineTransformer(response, rootUrl)) {
			if (legacy) {
				String sessionId = getSessionId(parameterMap);
				if (null != sessionId) {
					parameterMap.put(SESSION_ID, sessionId);
					addContentHeader(response);
					streamResults(transformer, BaseDao.FLOW_LINES_LEGACY, parameterMap);
				} else {
					response.setStatus(HttpStatus.BAD_REQUEST.value());
				}
			} else {
				parameterMap.put(Parameters.COMID, NumberUtils.parseNumber(comid, Integer.class));
				addContentHeader(response);
				streamResults(transformer, BaseDao.FLOW_LINES, parameterMap);
			}

		} catch (Throwable e) {
			LOG.error(e.getLocalizedMessage());
			response.sendError(HttpStatus.BAD_REQUEST.value(), e.getLocalizedMessage());
		}
	}

	protected void streamFeatures(HttpServletResponse response,
			String comid, String navigationMode, String stopComid, String distance, String dataSource, boolean legacy) throws IOException {
		Map<String, Object> parameterMap = parameters.processParameters(comid, navigationMode, distance, stopComid);
		try (FeatureTransformer transformer = new FeatureTransformer(response, rootUrl)) {
			if (legacy) {
				String sessionId = getSessionId(parameterMap);
				if (null != sessionId) {
					parameterMap.put(SESSION_ID, sessionId);
					parameterMap.put(DATA_SOURCE, dataSource.toLowerCase());
					addContentHeader(response);
					streamResults(transformer, BaseDao.FEATURES_LEGACY, parameterMap);
				} else {
					response.setStatus(HttpStatus.BAD_REQUEST.value());
				}
			} else {
				parameterMap.put(Parameters.COMID, NumberUtils.parseNumber(comid, Integer.class));
				parameterMap.put(DATA_SOURCE, dataSource.toLowerCase());
				addContentHeader(response);
				streamResults(transformer, BaseDao.FEATURES, parameterMap);
			}
	
		} catch (Throwable e) {
			LOG.error(e.getLocalizedMessage());
			response.sendError(HttpStatus.BAD_REQUEST.value(), e.getLocalizedMessage());
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

	protected String getSessionId(Map<String, Object> parameterMap) throws Exception {
		// No NPE testing - should never get here without parameters.
		int key = parameterMap.hashCode();
		Map<String, String> navigationResult = lockManager.executeLocked(key, () -> navigation.navigate(parameterMap));
		return navigation.interpretResult(navigationResult);
	}

	protected boolean isLegacy(String legacy, String navigationMode) {
		return (StringUtils.hasText(legacy) && "true".contentEquals(legacy.trim().toLowerCase()))
				|| NavigationMode.PP.toString().equalsIgnoreCase(navigationMode);
	}
}
