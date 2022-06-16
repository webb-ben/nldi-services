package gov.usgs.owi.nldi.controllers;

import gov.usgs.owi.nldi.dao.LookupDao;
import gov.usgs.owi.nldi.dao.NavigationDao;
import gov.usgs.owi.nldi.dao.StreamingDao;
import gov.usgs.owi.nldi.model.Comid;
import gov.usgs.owi.nldi.services.*;
import gov.usgs.owi.nldi.swagger.model.Feature;
import gov.usgs.owi.nldi.transform.HydrolocationTransformer;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.math.BigInteger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Pattern;
import mil.nga.sf.geojson.Position;
import org.hibernate.validator.constraints.Range;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class NetworkController extends BaseController {
  private static final Logger LOG = LoggerFactory.getLogger(NetworkController.class);

  @Autowired
  public NetworkController(
      LookupDao inLookupDao,
      StreamingDao inStreamingDao,
      NavigationDao inNavigationDao,
      Navigation inNavigation,
      Parameters inParameters,
      ConfigurationService configurationService,
      LogService inLogService,
      PyGeoApiService inPygeoapiService) {
    super(
        inLookupDao,
        inStreamingDao,
        inNavigationDao,
        inNavigation,
        inParameters,
        configurationService,
        inLogService,
        inPygeoapiService);
  }

  // swagger documentation for /linked-data/comid/{comid} endpoint
  @Operation(
      summary = "getComid",
      description = "returns comid as WGS84 lat/lon GeoJSON if it exists")
  @GetMapping(
      value = "linked-data/comid/{comid}",
      produces = {MIME_TYPE_GEOJSON, MediaType.APPLICATION_JSON_VALUE})
  public @ResponseBody Comid getComid(
      HttpServletRequest request,
      HttpServletResponse response,
      @PathVariable(Parameters.COMID) @Schema(example = "13294288") Integer comid) {

    BigInteger logId = logService.logRequest(request);
    Comid result = null;

    try {
      result = lookupDao.getComid(comid);
      result.setNavigation(createNavigationUrl(request.getRequestURI()));
    } finally {
      logService.logRequestComplete(logId, response.getStatus());
    }

    return result;
  }

  // swagger documentation for /linked-data/{featureSource}/{featureID}/navigate/{navigationMode}
  // endpoint
  @Operation(
      summary = "getFlowlines (deprecated)",
      description = "returns the flowlines for the specified navigation in WGS84 lat/lon GeoJSON")
  @GetMapping(
      value = "linked-data/comid/{comid}/navigate/{navigationMode}",
      produces = {MediaType.APPLICATION_JSON_VALUE, BaseController.MIME_TYPE_GEOJSON})
  @Deprecated
  @Hidden
  public void getFlowlines(
      HttpServletRequest request,
      HttpServletResponse response,
      @PathVariable(Parameters.COMID) @Range(min = 1, max = Integer.MAX_VALUE) String comid,
      @PathVariable(Parameters.NAVIGATION_MODE) @Pattern(regexp = REGEX_NAVIGATION_MODE)
          String navigationMode,
      @RequestParam(value = Parameters.STOP_COMID, required = false)
          @Range(min = 1, max = Integer.MAX_VALUE)
          String stopComid,
      @Parameter(description = Parameters.DISTANCE_DESCRIPTION)
          @RequestParam(
              value = Parameters.DISTANCE,
              required = false,
              defaultValue = Parameters.MAX_DISTANCE)
          @Pattern(
              message = Parameters.DISTANCE_VALIDATION_MESSAGE,
              regexp = Parameters.DISTANCE_VALIDATION_REGEX)
          String distance,
      @RequestParam(value = Parameters.LEGACY, required = false) String legacy)
      throws Exception {

    BigInteger logId = logService.logRequest(request);
    if (stopComid != null && (Integer.parseInt(stopComid) < Integer.parseInt(comid))) {
      logService.logRequestComplete(logId, HttpStatus.BAD_REQUEST.value());
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, BaseController.COMID_MISMATCH_ERROR);
    }

    try {
      streamFlowLines(
          response, comid, navigationMode, stopComid, distance, isLegacy(legacy, navigationMode));
    } finally {
      logService.logRequestComplete(logId, response.getStatus());
    }
  }

  // swagger documentation for /linked-data/{featureSource}/{featureID}/navigation/{navigationMode}
  // endpoint
  @Operation(
      summary = "getFlowlines",
      description = "returns the flowlines for the specified navigation in WGS84 lat/lon GeoJSON")
  @GetMapping(
      value = "linked-data/comid/{comid}/navigation/{navigationMode}/flowlines",
      produces = {MediaType.APPLICATION_JSON_VALUE, BaseController.MIME_TYPE_GEOJSON})
  public void getNavigationFlowlines(
      HttpServletRequest request,
      HttpServletResponse response,
      @PathVariable(Parameters.COMID)
          @Range(min = 1, max = Integer.MAX_VALUE)
          @Schema(example = "13294314")
          String comid,
      @PathVariable(Parameters.NAVIGATION_MODE)
          @Pattern(regexp = REGEX_NAVIGATION_MODE)
          @Schema(
              example = "UM",
              allowableValues = {"UM", "UT", "DM", "DD"})
          String navigationMode,
      @RequestParam(value = Parameters.STOP_COMID, required = false)
          @Range(min = 1, max = Integer.MAX_VALUE)
          String stopComid,
      @Parameter(description = Parameters.DISTANCE_DESCRIPTION_NEW)
          @RequestParam(value = Parameters.DISTANCE)
          @Pattern(
              message = Parameters.DISTANCE_VALIDATION_MESSAGE,
              regexp = Parameters.DISTANCE_VALIDATION_REGEX)
          @Schema(example = "50")
          String distance,
      @RequestParam(value = Parameters.LEGACY, required = false) String legacy)
      throws Exception {

    BigInteger logId = logService.logRequest(request);
    if (stopComid != null && (Integer.parseInt(stopComid) < Integer.parseInt(comid))) {
      logService.logRequestComplete(logId, HttpStatus.BAD_REQUEST.value());
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, BaseController.COMID_MISMATCH_ERROR);
    }
    try {

      streamFlowLines(
          response, comid, navigationMode, stopComid, distance, isLegacy(legacy, navigationMode));
    } finally {
      logService.logRequestComplete(logId, response.getStatus());
    }
  }

  // swagger documentation for
  // /linked-data/{featureSource}/{featureID}/navigate/{navigationMode}/{dataSource} endpoint
  @Operation(
      summary = "getFeatures (deprecated)",
      description =
          "Returns all features found along the specified navigation as points in WGS84 lat/lon"
              + " GeoJSON")
  @GetMapping(
      value = "linked-data/comid/{comid}/navigate/{navigationMode}/{dataSource}",
      produces = {MediaType.APPLICATION_JSON_VALUE, BaseController.MIME_TYPE_GEOJSON})
  @Deprecated
  @Hidden
  public void getFeaturesDeprecated(
      HttpServletRequest request,
      HttpServletResponse response,
      @PathVariable(Parameters.COMID) @Range(min = 1, max = Integer.MAX_VALUE) String comid,
      @PathVariable(Parameters.NAVIGATION_MODE) @Pattern(regexp = REGEX_NAVIGATION_MODE)
          String navigationMode,
      @PathVariable(value = DATA_SOURCE) String dataSource,
      @RequestParam(value = Parameters.STOP_COMID, required = false)
          @Range(min = 1, max = Integer.MAX_VALUE)
          String stopComid,
      @Parameter(description = Parameters.DISTANCE_DESCRIPTION)
          @RequestParam(
              value = Parameters.DISTANCE,
              required = false,
              defaultValue = Parameters.MAX_DISTANCE)
          @Pattern(
              message = Parameters.DISTANCE_VALIDATION_MESSAGE,
              regexp = Parameters.DISTANCE_VALIDATION_REGEX)
          String distance,
      @RequestParam(value = Parameters.LEGACY, required = false) String legacy)
      throws Exception {
    BigInteger logId = logService.logRequest(request);
    if (stopComid != null && (Integer.parseInt(stopComid) < Integer.parseInt(comid))) {
      logService.logRequestComplete(logId, HttpStatus.BAD_REQUEST.value());
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, BaseController.COMID_MISMATCH_ERROR);
    }

    try {
      streamFeatures(
          response,
          comid,
          navigationMode,
          stopComid,
          distance,
          dataSource,
          isLegacy(legacy, navigationMode));
    } finally {
      logService.logRequestComplete(logId, response.getStatus());
    }
  }

  // swagger documentation for
  // /linked-data/{featureSource}/{featureID}/navigation/{navigationMode}/{dataSource} endpoint
  @Operation(
      summary = "getFeatures",
      description =
          "Returns all features found along the specified navigation as points in WGS84 lat/lon"
              + " GeoJSON")
  @GetMapping(
      value = "linked-data/comid/{comid}/navigation/{navigationMode}/{dataSource}",
      produces = {MediaType.APPLICATION_JSON_VALUE, BaseController.MIME_TYPE_GEOJSON})
  public void getFeatures(
      HttpServletRequest request,
      HttpServletResponse response,
      @PathVariable(Parameters.COMID)
          @Range(min = 1, max = Integer.MAX_VALUE)
          @Schema(example = "13294314")
          String comid,
      @PathVariable(Parameters.NAVIGATION_MODE)
          @Pattern(regexp = REGEX_NAVIGATION_MODE)
          @Schema(
              example = "UM",
              allowableValues = {"UM", "UT", "DM", "DD"})
          String navigationMode,
      @PathVariable(value = DATA_SOURCE) @Schema(example = "wqp") String dataSource,
      @RequestParam(value = Parameters.STOP_COMID, required = false)
          @Range(min = 1, max = Integer.MAX_VALUE)
          String stopComid,
      @Parameter(description = Parameters.DISTANCE_DESCRIPTION_NEW)
          @RequestParam(value = Parameters.DISTANCE)
          @Pattern(
              message = Parameters.DISTANCE_VALIDATION_MESSAGE,
              regexp = Parameters.DISTANCE_VALIDATION_REGEX)
          @Schema(example = "50")
          String distance,
      @RequestParam(value = Parameters.LEGACY, required = false) String legacy)
      throws Exception {

    BigInteger logId = logService.logRequest(request);
    if (stopComid != null && (Integer.parseInt(stopComid) < Integer.parseInt(comid))) {
      logService.logRequestComplete(logId, HttpStatus.BAD_REQUEST.value());
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, BaseController.COMID_MISMATCH_ERROR);
    }
    try {
      lookupDao.validateDataSource(dataSource);

      streamFeatures(
          response,
          comid,
          navigationMode,
          stopComid,
          distance,
          dataSource,
          isLegacy(legacy, navigationMode));
    } finally {
      logService.logRequestComplete(logId, response.getStatus());
    }
  }

  // swagger documentation for /linked-data/comid/position endpoint
  @Operation(
      summary = "getFeatureByCoordinates",
      description = "returns the feature closest to a provided set of coordinates")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "OK",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = Feature.class))
            }),
        @ApiResponse(responseCode = "500", description = "Server error", content = @Content)
      })
  @GetMapping(
      value = "linked-data/comid/position",
      produces = {MediaType.APPLICATION_JSON_VALUE, BaseController.MIME_TYPE_GEOJSON})
  public @ResponseBody Comid getFeatureByCoordinates(
      HttpServletRequest request,
      HttpServletResponse response,
      @Parameter(description = Parameters.COORDS_DESCRIPTION)
          @Pattern(
              message = Parameters.POINT_VALIDATION_MESSAGE,
              regexp = Parameters.POINT_VALIDATION_REGEX)
          @RequestParam(value = Parameters.COORDS)
          @Schema(example = "POINT(-89.509 43.087)")
          String coords) {
    BigInteger logId = logService.logRequest(request);
    Position position = extractLatitudeAndLongitude(coords);
    try {
      Integer comid = lookupDao.getComidByLatitudeAndLongitude(position);

      Comid comidFeature = lookupDao.getComid(comid);
      String navigationUrl = createNavigationUrl(request.getRequestURI());
      navigationUrl = navigationUrl.replaceFirst("position", comidFeature.getComid().toString());
      comidFeature.setNavigation(navigationUrl);
      return comidFeature;
    } finally {
      logService.logRequestComplete(logId, response.getStatus());
    }
  }

  @Operation(
      summary = "getHydrologicLocation",
      description = "Returns the hydrologic location closest to a provided set of coordinates.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Hydrolocation found",
            content = {
              @Content(
                  mediaType = MIME_TYPE_GEOJSON,
                  schema = @Schema(implementation = Feature.class))
            }),
        @ApiResponse(responseCode = "500", description = "Server error", content = @Content)
      })
  @GetMapping(
      value = "linked-data/hydrolocation",
      produces = {MediaType.APPLICATION_JSON_VALUE, BaseController.MIME_TYPE_GEOJSON})
  public void getHydrologicLocation(
      HttpServletRequest request,
      HttpServletResponse response,
      @Parameter(description = Parameters.COORDS_DESCRIPTION)
          @Pattern(
              message = Parameters.POINT_VALIDATION_MESSAGE,
              regexp = Parameters.POINT_VALIDATION_REGEX)
          @RequestParam(value = Parameters.COORDS)
          @Schema(example = "POINT(-89.509 43.087)")
          String coords)
      throws Exception {
    BigInteger logId = logService.logRequest(request);
    Position providedPoint = extractLatitudeAndLongitude(coords);

    try {
      Position flowtraceResponse =
          pygeoapiService.getNldiFlowTraceIntersectionPoint(
              providedPoint, true, PyGeoApiService.Direction.NONE);

      Integer comid = lookupDao.getComidByLatitudeAndLongitude(providedPoint);
      String measure = lookupDao.getMeasure(comid, flowtraceResponse);
      String reachcode = lookupDao.getReachCode(comid);

      Position indexedPoint = flowtraceResponse;

      addContentHeader(response);
      HydrolocationTransformer transformer =
          new HydrolocationTransformer(response, configurationService);
      transformer.writeIndexedFeature(indexedPoint, comid.toString(), reachcode, measure);
      transformer.writeProvidedFeature(providedPoint);
      transformer.end();
      response.setStatus(HttpStatus.OK.value());
    } finally {
      logService.logRequestComplete(logId, response.getStatus());
    }
  }

  private Position extractLatitudeAndLongitude(String coords) {
    // Only currently supported format is POINT(x y)
    String tempCoords = coords.replaceAll("POINT ?\\(|\\)", "");
    String[] coordsArray = tempCoords.split(" ");
    Double longitude = Double.parseDouble(coordsArray[0]);
    Double latitude = Double.parseDouble(coordsArray[1]);
    if (longitude < -180 || longitude > 180) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid longitude");
    }
    if (latitude < -90 || latitude > 90) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid latitude");
    }

    Position position = new Position(longitude, latitude);
    return position;
  }
}
