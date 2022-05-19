package gov.usgs.owi.nldi.controllers;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import gov.usgs.owi.nldi.dao.LookupDao;
import gov.usgs.owi.nldi.dao.StreamingDao;
import gov.usgs.owi.nldi.exceptions.DataSourceNotFoundException;
import gov.usgs.owi.nldi.services.*;
import java.math.BigInteger;
import javax.servlet.http.HttpServletRequest;
import mil.nga.sf.geojson.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(NetworkController.class)
public class NetworkControllerTest {

  @Autowired private MockMvc mvc;

  @MockBean private LookupDao lookupDao;
  @MockBean private StreamingDao streamingDao;
  @MockBean private Navigation navigation;
  @MockBean private Parameters parameters;
  @MockBean private ConfigurationService configurationService;
  @MockBean private LogService logService;
  @MockBean private PyGeoApiService pygeoapiService;

  @BeforeEach
  public void setUp() {
    when(logService.logRequest(any(HttpServletRequest.class))).thenReturn(BigInteger.ONE);
    doThrow(new DataSourceNotFoundException("invalid-source"))
        .when(lookupDao)
        .validateDataSource(eq("invalid-source"));
  }

  @Test
  public void getFlowlinesTest() throws Exception {
    // missing required parameter
    mvc.perform(get("/linked-data/comid/13297246/navigation/PP/flowlines"))
        .andExpect(status().isBadRequest())
        .andExpect(content().string("Required String parameter 'distance' is not present"));

    verify(logService, times(0)).logRequest(any(HttpServletRequest.class));
    verify(logService, times(0)).logRequestComplete(any(BigInteger.class), any(int.class));

    // valid example
    mvc.perform(
            get(
                "/linked-data/comid/13297246/navigation/PP/flowlines?stopComid=13297247&distance=1"))
        .andExpect(status().isOk());

    verify(logService, times(1)).logRequest(any(HttpServletRequest.class));
    verify(logService, times(1))
        .logRequestComplete(any(BigInteger.class), eq(HttpStatus.OK.value()));
  }

  @Test
  public void getFeaturesTest() throws Exception {
    mvc.perform(get("/linked-data/comid/13294314/navigation/UT/wqp?distance=1"))
        .andExpect(status().isOk());

    verify(logService, times(1)).logRequest(any(HttpServletRequest.class));
    verify(logService, times(1))
        .logRequestComplete(any(BigInteger.class), eq(HttpStatus.OK.value()));

    mvc.perform(get("/linked-data/comid/13294314/navigation/UT/invalid-source?distance=1"))
        .andExpect(status().isNotFound())
        .andExpect(content().string("The data source \"invalid-source\" does not exist."));

    verify(logService, times(2)).logRequest(any(HttpServletRequest.class));
    verify(logService, times(2)).logRequestComplete(any(BigInteger.class), anyInt());
  }

  @Test
  public void invalidStopComidTest() throws Exception {
    mvc.perform(get("/linked-data/comid/13294314/navigation/UT/wqp?stopComid=13294313&distance=1"))
        .andExpect(status().isBadRequest())
        .andExpect(
            content()
                .string(
                    "400 BAD_REQUEST \"The stopComid must be downstream of the start comid.\""));

    verify(logService, times(1)).logRequest(any(HttpServletRequest.class));
    verify(logService, times(1))
        .logRequestComplete(any(BigInteger.class), eq(HttpStatus.BAD_REQUEST.value()));
  }

  @Test
  public void getHydrolocationTest() throws Exception {
    Integer comid = 15510512;
    String lon = "-74.71";
    String lat = "43.743";
    Position pygeoResponse = new Position(-74.7147222, 43.74305556);

    // mock calls to other classes
    when(lookupDao.getComidByLatitudeAndLongitude(any(Position.class))).thenReturn(comid);
    when(lookupDao.getMeasure(eq(comid), eq(pygeoResponse))).thenReturn("measure");
    when(lookupDao.getReachCode(eq(comid))).thenReturn("reachcode");
    when(pygeoapiService.getNldiFlowTraceIntersectionPoint(
            any(Position.class), eq(true), eq(PyGeoApiService.Direction.NONE)))
        .thenReturn(pygeoResponse);

    mvc.perform(get(String.format("/linked-data/hydrolocation?coords=POINT(%s %s)", lon, lat)))
        .andExpect(status().isOk());

    verify(logService, times(1)).logRequest(any(HttpServletRequest.class));
    verify(logService, times(1))
        .logRequestComplete(any(BigInteger.class), eq(HttpStatus.OK.value()));
  }
}
