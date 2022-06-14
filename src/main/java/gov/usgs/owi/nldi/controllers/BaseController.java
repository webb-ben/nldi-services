package gov.usgs.owi.nldi.controllers;

import de.jkeylockmanager.manager.KeyLockManager;
import de.jkeylockmanager.manager.KeyLockManagers;
import gov.usgs.owi.nldi.NavigationMode;
import gov.usgs.owi.nldi.dao.BaseDao;
import gov.usgs.owi.nldi.dao.LookupDao;
import gov.usgs.owi.nldi.dao.StreamingDao;
import gov.usgs.owi.nldi.dao.StreamingResultHandler;
import gov.usgs.owi.nldi.services.*;
import gov.usgs.owi.nldi.transform.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.apache.ibatis.session.ResultHandler;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Validated
@EnableWebMvc
public abstract class BaseController {
  private static final Logger LOG = LoggerFactory.getLogger(BaseController.class);

  public static final String HEADER_CONTENT_TYPE = "Content-Type";
  public static final String MIME_TYPE_GEOJSON = "application/vnd.geo+json";
  public static final String MIME_TYPE_JSONLD = "application/ld+json";
  public static final String REGEX_NAVIGATION_MODE = "DD|DM|PP|UT|UM";
  public static final String DATA_SOURCE = "dataSource";
  public static final String COMID_MISMATCH_ERROR =
      "The stopComid must be downstream of the start comid.";

  protected final LookupDao lookupDao;
  protected final StreamingDao streamingDao;
  protected final Navigation navigation;
  protected final Parameters parameters;
  protected final ConfigurationService configurationService;
  protected final LogService logService;
  protected final PyGeoApiService pygeoapiService;

  private final KeyLockManager lockManager = KeyLockManagers.newLock();

  public BaseController(
      LookupDao inLookupDao,
      StreamingDao inStreamingDao,
      Navigation inNavigation,
      Parameters inParameters,
      ConfigurationService inConfigurationService,
      LogService inLogService,
      PyGeoApiService inPygeoapiService) {
    lookupDao = inLookupDao;
    streamingDao = inStreamingDao;
    navigation = inNavigation;
    parameters = inParameters;
    configurationService = inConfigurationService;
    logService = inLogService;
    pygeoapiService = inPygeoapiService;
  }

  protected void streamFlowLines(
      HttpServletResponse response,
      String comid,
      String navigationMode,
      String stopComid,
      String distance,
      boolean legacy)
      throws Exception {

    String featureType;
    Map<String, Object> parameterMap =
        parameters.processParameters(comid, navigationMode, distance, stopComid);

    if (legacy) {
      String sessionId = getSessionId(parameterMap, response);
      if (null != sessionId) {
        parameterMap.put(StreamingDao.SESSION_ID, sessionId);
        featureType = BaseDao.FLOW_LINES_LEGACY;
      } else {
        return;
      }
    } else {
      featureType = BaseDao.FLOW_LINES;
    }

    // Defer transformer creation to allow error messages to be sent (above) in the response if
    // needed
    FlowLineTransformer transformer = new FlowLineTransformer(response);

    addContentHeader(response);
    streamResults(transformer, featureType, parameterMap);
  }

  protected void streamFlowLines(
      HttpServletResponse response,
      String comid,
      String navigationMode,
      String stopComid,
      String distance,
      String measure,
      String trimTolerance,
      boolean legacy)
      throws Exception {

    String featureType;
    Map<String, Object> parameterMap =
        parameters.processParameters(
            comid, navigationMode, distance, stopComid, measure, trimTolerance);

    if (!parameterMap.isEmpty() && parameterMap.get(Parameters.TRIM_TOLERANCE) == null) {
      parameterMap.put(Parameters.TRIM_TOLERANCE, Parameters.TRIM_TOLERANCE_DEFAULT);
    }

    if (legacy) {
      String sessionId = getSessionId(parameterMap, response);
      if (null != sessionId) {
        parameterMap.put(StreamingDao.SESSION_ID, sessionId);
        featureType = BaseDao.FLOW_LINES_LEGACY;
      } else {
        return;
      }
    } else {
      featureType = BaseDao.FLOW_LINES;
    }

    // Defer transformer creation to allow error messages to be sent (above) in the response if
    // needed
    FlowLineTransformer transformer = new FlowLineTransformer(response);

    addContentHeader(response);
    streamResults(transformer, featureType, parameterMap);
  }

  protected void streamFeatures(
      HttpServletResponse response,
      String comid,
      String navigationMode,
      String stopComid,
      String distance,
      String dataSource,
      boolean legacy)
      throws Exception {

    String featureType;
    Map<String, Object> parameterMap =
        parameters.processParameters(comid, navigationMode, distance, stopComid);

    if (legacy) {
      String sessionId = getSessionId(parameterMap, response);
      if (null != sessionId) {
        parameterMap.put(StreamingDao.SESSION_ID, sessionId);
        featureType = BaseDao.FEATURES_LEGACY;
      } else {
        return;
      }
    } else {
      featureType = BaseDao.FEATURES;
    }

    // Defer transformer creation to allow error messages to be sent (above) in the response if
    // needed
    FeatureTransformer transformer = new FeatureTransformer(response, configurationService);

    parameterMap.put(DATA_SOURCE, dataSource.toLowerCase());
    addContentHeader(response);
    streamResults(transformer, featureType, parameterMap);
  }

  protected void streamBasin(HttpServletResponse response, Integer comid, Boolean simplified)
      throws Exception {
    Map<String, Object> parameterMap = new HashMap<>();
    parameterMap.put(Parameters.COMID, comid);
    parameterMap.put(Parameters.SIMPLIFIED, simplified);
    BasinTransformer transformer = new BasinTransformer(response);
    addContentHeader(response);
    streamResults(transformer, BaseDao.BASIN, parameterMap);
  }

  protected void streamResults(
      ITransformer transformer, String featureType, Map<String, Object> parameterMap) {
    LOG.trace("start streaming");
    ResultHandler<?> handler = new StreamingResultHandler(transformer);
    streamingDao.stream(featureType, parameterMap, handler);
    transformer.end();
    LOG.trace("done streaming");
  }

  protected void handleSplitCatchmentResponse(
      JSONObject splitCatchmentResponse, HttpServletResponse response) throws IOException {
    addContentHeader(response);
    SplitCatchmentTransformer transformer = new SplitCatchmentTransformer(response);
    transformer.write(splitCatchmentResponse);
    transformer.end();
  }

  protected void addContentHeader(HttpServletResponse response) {
    response.setHeader(HEADER_CONTENT_TYPE, MIME_TYPE_GEOJSON);
  }

  protected String getSessionId(Map<String, Object> parameterMap, HttpServletResponse response)
      throws Exception {
    // No NPE testing - should never get here without parameters.
    int key = parameterMap.hashCode();
    Map<String, String> navigationResult =
        lockManager.executeLocked(key, () -> navigation.navigate(parameterMap));
    return navigation.interpretResult(navigationResult, response);
  }

  protected boolean isLegacy(String legacy, String navigationMode) {
    return (StringUtils.hasText(legacy) && "true".contentEquals(legacy.trim().toLowerCase()))
        || NavigationMode.PP.toString().equalsIgnoreCase(navigationMode);
  }

  protected String createNavigationUrl(@NonNull String requestUrl) {
    String newUrl = configurationService.getLinkedDataUrl();
    String[] arr = requestUrl.split("linked-data");
    newUrl += arr[1];
    newUrl += "/navigation";
    return newUrl;
  }
}
