package gov.usgs.owi.nldi.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import gov.usgs.owi.nldi.dao.LookupDao;
import gov.usgs.owi.nldi.dao.StreamingDao;
import gov.usgs.owi.nldi.services.*;
import java.math.BigInteger;
import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(LookupController.class)
public class LookupControllerTest {

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
    when(configurationService.getRootUrl()).thenReturn("http://owi-test.usgs.gov:8080/test-url");
    when(logService.logRequest(any(HttpServletRequest.class))).thenReturn(BigInteger.ONE);
  }

  @Test
  public void getLookupsTest() throws Exception {
    // missing required parameter
    mvc.perform(get("/lookups")).andExpect(status().isOk());

    verify(logService, times(1)).logRequest(any(HttpServletRequest.class));
    verify(logService, times(1))
        .logRequestComplete(any(BigInteger.class), eq(HttpStatus.OK.value()));
  }

  @Test
  public void getLookupsRedirectTest() throws Exception {
    // missing required parameter
    mvc.perform(get("/lookups/char-type"))
        .andExpect(status().isFound())
        .andExpect(
            redirectedUrl(
                "http://owi-test.usgs.gov:8080/test-url/lookups/char-type/characteristics?f=json"));

    verify(logService, times(1)).logRequest(any(HttpServletRequest.class));
    verify(logService, times(1))
        .logRequestComplete(any(BigInteger.class), eq(HttpStatus.FOUND.value()));
  }

  @Test
  public void getCharacteristicsTest() throws Exception {
    // missing required parameter
    mvc.perform(get("/lookups/char-type/characteristics")).andExpect(status().isOk());

    verify(logService, times(1)).logRequest(any(HttpServletRequest.class));
    verify(logService, times(1))
        .logRequestComplete(any(BigInteger.class), eq(HttpStatus.OK.value()));
  }
}
