package gov.usgs.owi.nldi.controllers;

import gov.usgs.owi.nldi.dao.BaseDao;
import gov.usgs.owi.nldi.dao.LookupDao;
import gov.usgs.owi.nldi.dao.StreamingDao;
import gov.usgs.owi.nldi.services.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class NetworkControllerTest {

  private StreamingDao streamingDao;
  @Mock private LookupDao lookupDao;
  @Mock private Navigation navigation;
  @Mock private Parameters parameters;
  @Mock private LogService logService;
  @Mock private PyGeoApiService pygeoapiService;

  private TestConfigurationService configurationService;
  private NetworkController controller;
  private MockHttpServletResponse response;
  private MockHttpServletRequest request;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    configurationService = new TestConfigurationService();
    controller =
        new NetworkController(
            lookupDao,
            streamingDao,
            navigation,
            parameters,
            configurationService,
            logService,
            pygeoapiService);
    response = new MockHttpServletResponse();
    request = new MockHttpServletRequest();

    when(logService.logRequest(any(HttpServletRequest.class))).thenReturn(BigInteger.ONE);
  }

  @Test
  public void getFlowlinesTest() throws Exception {
    try {
      controller.getFlowlines(request, response, "13297246", "PP", "13297198", null, null);
      fail("should have failed");
    } catch (ResponseStatusException rse) {
      // good
    } catch (Throwable t) {
      fail(t);
    }
    verify(logService).logRequest(any(HttpServletRequest.class));
    verify(logService)
        .logRequestComplete(any(BigInteger.class), eq(HttpStatus.BAD_REQUEST.value()));
  }

  @Test
  public void getFeaturesTest() throws Exception {
    controller.getFeatures(request, response, null, null, null, null, null, null);
    verify(logService).logRequest(any(HttpServletRequest.class));
    verify(logService).logRequestComplete(any(BigInteger.class), any(int.class));
    // this is a INTERNAL_SERVER_ERROR because of NPEs that shouldn't happen in real life.
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getStatus());
  }

  @Test
  public void getBasinTest() throws Exception {
    controller.getFeatures(request, response, null, null, BaseDao.BASIN, null, null, null);
    verify(logService).logRequest(any(HttpServletRequest.class));
    verify(logService).logRequestComplete(any(BigInteger.class), any(int.class));
    // this is a INTERNAL_SERVER_ERROR because of NPEs that shouldn't happen in real life.
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getStatus());
  }

  @Test
  public void getHydrolocationTest() throws Exception {
    Integer comid = 15510512;
    String lon = "-74.71";
    String lat = "43.743";
    Map<String, String> pygeoResponse = new HashMap<>();
    pygeoResponse.put("lat", "43.74305556");
    pygeoResponse.put("lon", "-74.7147222");

	// mock calls to other classes
    when(lookupDao.getComidByLatitudeAndLongitude(any())).thenReturn(comid);
    when(lookupDao.getMeasure(comid, lat, lon)).thenReturn("measure");
    when(lookupDao.getReachCode(comid)).thenReturn("reachcode");
    when(pygeoapiService.getNldiFlowTraceIntersectionPoint(
            lat, lon, true, PyGeoApiService.Direction.NONE))
        .thenReturn(pygeoResponse);

    controller.getHydrologicLocation(request, response, String.format("POINT (%s %s)", lon, lat));
    verify(logService).logRequest(any(HttpServletRequest.class));
    verify(logService).logRequestComplete(any(BigInteger.class), any(int.class));
    assertEquals(HttpStatus.OK.value(), response.getStatus());
  }
}
