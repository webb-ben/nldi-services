package gov.usgs.owi.nldi.controllers;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import gov.usgs.owi.nldi.dao.LookupDao;
import gov.usgs.owi.nldi.dao.StreamingDao;
import gov.usgs.owi.nldi.exceptions.DataSourceNotFoundException;
import gov.usgs.owi.nldi.exceptions.FeatureIdNotFoundException;
import gov.usgs.owi.nldi.exceptions.FeatureSourceNotFoundException;
import gov.usgs.owi.nldi.services.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(LinkedDataController.class)
public class LinkedDataControllerTest {

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
    when(configurationService.getLinkedDataUrl())
        .thenReturn("http://owi-test.usgs.gov:8080/test-url/linked-data");
    when(logService.logRequest(any(HttpServletRequest.class))).thenReturn(BigInteger.ONE);
    when(lookupDao.getList(any(String.class), anyMap())).thenReturn(getTestList());
    doThrow(new FeatureSourceNotFoundException("invalid-source"))
        .when(lookupDao)
        .validateFeatureSource(eq("invalid-source"));
    doThrow(new FeatureIdNotFoundException("source", "invalid-id"))
        .when(lookupDao)
        .validateFeatureSourceAndId(anyString(), eq("invalid-id"));
    doThrow(new DataSourceNotFoundException("invalid-source"))
        .when(lookupDao)
        .validateDataSource(eq("invalid-source"));
  }

  @Test
  public void getDataSourcesTest() throws Exception {
    mvc.perform(get("/linked-data"))
        .andExpect(status().isOk())
        .andExpect(
            content()
                .string(
                    "[{\"source\":\"comid\",\"sourceName\":\"NHDPlus"
                        + " comid\",\"features\":\"http://owi-test.usgs.gov:8080/test-url/linked-data/comid\"},{\"features\":\"features-url\",\"source\":\"source\",\"sourceName\":\"source"
                        + " name\"}]"));

    verify(logService).logRequest(any(HttpServletRequest.class));
    verify(logService).logRequestComplete(any(BigInteger.class), any(int.class));
  }

  @Test
  public void getFeaturesTest() throws Exception {
    mvc.perform(get("/linked-data/nwissite")).andExpect(status().isOk());

    verify(logService, times(1)).logRequest(any(HttpServletRequest.class));
    verify(logService, times(1))
        .logRequestComplete(any(BigInteger.class), eq(HttpStatus.OK.value()));
  }

  @Test
  public void getRegisteredFeatureTest() throws Exception {
    mvc.perform(get("/linked-data/valid-source/valid-id")).andExpect(status().isOk());

    verify(logService, times(1)).logRequest(any(HttpServletRequest.class));
    verify(logService, times(1)).logRequestComplete(any(BigInteger.class), anyInt());

    mvc.perform(get("/linked-data/valid-source/invalid-id"))
        .andExpect(status().isNotFound())
        .andExpect(
            content()
                .string(
                    "The feature ID \"invalid-id\" does not exist in feature source \"source\"."));

    verify(logService, times(2)).logRequest(any(HttpServletRequest.class));
    verify(logService, times(2)).logRequestComplete(any(BigInteger.class), anyInt());

    mvc.perform(get("/linked-data/invalid-source/valid-id"))
        .andExpect(status().isNotFound())
        .andExpect(content().string("The feature source \"invalid-source\" does not exist."));

    verify(logService, times(3)).logRequest(any(HttpServletRequest.class));
    verify(logService, times(3)).logRequestComplete(any(BigInteger.class), anyInt());
  }

  @Test
  public void getNavigateTypesTest() throws Exception {
    mvc.perform(get("/linked-data/valid-source/valid-id/navigate"))
        .andExpect(status().isOk())
        .andExpect(
            content()
                .string(
                    "{\"upstreamMain\":\"http://owi-test.usgs.gov:8080/test-url/linked-data/valid-source/valid-id/navigate/UM\",\"upstreamTributaries\":\"http://owi-test.usgs.gov:8080/test-url/linked-data/valid-source/valid-id/navigate/UT\",\"downstreamMain\":\"http://owi-test.usgs.gov:8080/test-url/linked-data/valid-source/valid-id/navigate/DM\",\"downstreamDiversions\":\"http://owi-test.usgs.gov:8080/test-url/linked-data/valid-source/valid-id/navigate/DD\"}"));

    verify(logService, times(1)).logRequest(any(HttpServletRequest.class));
    verify(logService, times(1))
        .logRequestComplete(any(BigInteger.class), eq(HttpStatus.OK.value()));
  }

  @Test
  public void getNavigationTypesTest() throws Exception {
    mvc.perform(get("/linked-data/valid-source/valid-id/navigation"))
        .andExpect(status().isOk())
        .andExpect(
            content()
                .string(
                    "{\"upstreamMain\":\"http://owi-test.usgs.gov:8080/test-url/linked-data/valid-source/valid-id/navigation/UM\",\"upstreamTributaries\":\"http://owi-test.usgs.gov:8080/test-url/linked-data/valid-source/valid-id/navigation/UT\",\"downstreamMain\":\"http://owi-test.usgs.gov:8080/test-url/linked-data/valid-source/valid-id/navigation/DM\",\"downstreamDiversions\":\"http://owi-test.usgs.gov:8080/test-url/linked-data/valid-source/valid-id/navigation/DD\"}"));

    verify(logService, times(1)).logRequest(any(HttpServletRequest.class));
    verify(logService, times(1))
        .logRequestComplete(any(BigInteger.class), eq(HttpStatus.OK.value()));
  }

  @Test
  public void getCharacteristicDataTest() throws Exception {
    mvc.perform(get("/linked-data/valid-source/valid-id/char-type")).andExpect(status().isOk());

    verify(logService, times(1)).logRequest(any(HttpServletRequest.class));
    verify(logService, times(1))
        .logRequestComplete(any(BigInteger.class), eq(HttpStatus.OK.value()));

    mvc.perform(get("/linked-data/valid-source/valid-id/char-type?characteristicId=val1,val2"))
        .andExpect(status().isOk());

    verify(logService, times(2)).logRequest(any(HttpServletRequest.class));
    verify(logService, times(2))
        .logRequestComplete(any(BigInteger.class), eq(HttpStatus.OK.value()));
  }

  @Test
  public void getBasinTest() throws Exception {
    mvc.perform(get("/linked-data/valid-source/valid-id/basin")).andExpect(status().isOk());

    verify(logService, times(1)).logRequest(any(HttpServletRequest.class));
    verify(logService, times(1))
        .logRequestComplete(any(BigInteger.class), eq(HttpStatus.OK.value()));

    mvc.perform(
            get("/linked-data/valid-source/valid-id/basin?simplified=true&splitCatchment=false"))
        .andExpect(status().isOk());

    verify(logService, times(2)).logRequest(any(HttpServletRequest.class));
    verify(logService, times(2))
        .logRequestComplete(any(BigInteger.class), eq(HttpStatus.OK.value()));

    mvc.perform(
            get("/linked-data/valid-source/valid-id/basin?simplified=true&splitCatchment=invalid"))
        .andExpect(status().isBadRequest());

    mvc.perform(
            get("/linked-data/valid-source/valid-id/basin?simplified=invalid&splitCatchment=false"))
        .andExpect(status().isBadRequest());

    mvc.perform(get("/linked-data/invalid-source/valid-id/basin"))
        .andExpect(status().isNotFound())
        .andExpect(content().string("The feature source \"invalid-source\" does not exist."));

    verify(logService, times(3)).logRequest(any(HttpServletRequest.class));
    verify(logService, times(3)).logRequestComplete(any(BigInteger.class), anyInt());

    mvc.perform(get("/linked-data/valid-source/invalid-id/basin"))
        .andExpect(status().isNotFound())
        .andExpect(
            content()
                .string(
                    "The feature ID \"invalid-id\" does not exist in feature source \"source\"."));

    verify(logService, times(4)).logRequest(any(HttpServletRequest.class));
    verify(logService, times(4)).logRequestComplete(any(BigInteger.class), anyInt());
  }

  @Test
  public void getFlowlinesTest() throws Exception {
    mvc.perform(get("/linked-data/valid-source/valid-id/navigate/UM")).andExpect(status().isOk());

    verify(logService, times(1)).logRequest(any(HttpServletRequest.class));
    verify(logService, times(1))
        .logRequestComplete(any(BigInteger.class), eq(HttpStatus.OK.value()));

    mvc.perform(get("/linked-data/valid-source/valid-id/navigate/UM?stopComid=-1"))
        .andExpect(status().isBadRequest());

    mvc.perform(get("/linked-data/valid-source/valid-id/navigate/UM?stopComid=12345"))
        .andExpect(status().isOk());

    verify(logService, times(2)).logRequest(any(HttpServletRequest.class));
    verify(logService, times(2))
        .logRequestComplete(any(BigInteger.class), eq(HttpStatus.OK.value()));

    mvc.perform(get("/linked-data/valid-source/valid-id/navigate/UM?distance=bad-input"))
        .andExpect(status().isBadRequest());

    mvc.perform(get("/linked-data/valid-source/valid-id/navigate/UM?distance=5.678"))
        .andExpect(status().isOk());

    verify(logService, times(3)).logRequest(any(HttpServletRequest.class));
    verify(logService, times(3))
        .logRequestComplete(any(BigInteger.class), eq(HttpStatus.OK.value()));

    mvc.perform(get("/linked-data/valid-source/valid-id/navigate/UM?legacy=true"))
        .andExpect(status().isOk());

    verify(logService, times(4)).logRequest(any(HttpServletRequest.class));
    verify(logService, times(4))
        .logRequestComplete(any(BigInteger.class), eq(HttpStatus.OK.value()));

    mvc.perform(get("/linked-data/valid-source/valid-id/navigate/wrong"))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void getFeaturesNavigationTest() throws Exception {
    mvc.perform(get("/linked-data/valid-source/valid-id/navigation/UM/valid-source"))
        .andExpect(status().isBadRequest());

    mvc.perform(get("/linked-data/valid-source/valid-id/navigation/WRONG/valid-source?distance=1"))
        .andExpect(status().isBadRequest());

    mvc.perform(get("/linked-data/valid-source/valid-id/navigation/UM/valid-source?distance=1"))
        .andExpect(status().isOk());

    verify(logService, times(1)).logRequest(any(HttpServletRequest.class));
    verify(logService, times(1))
        .logRequestComplete(any(BigInteger.class), eq(HttpStatus.OK.value()));

    mvc.perform(
            get(
                "/linked-data/valid-source/valid-id/navigation/UM/valid-source?distance=1&stopComid=-1"))
        .andExpect(status().isBadRequest());

    mvc.perform(
            get(
                "/linked-data/valid-source/valid-id/navigation/UM/valid-source?distance=1&stopComid=12345"))
        .andExpect(status().isOk());

    verify(logService, times(2)).logRequest(any(HttpServletRequest.class));
    verify(logService, times(2))
        .logRequestComplete(any(BigInteger.class), eq(HttpStatus.OK.value()));

    mvc.perform(get("/linked-data/valid-source/valid-id/navigation/UM/invalid-source?distance=1"))
        .andExpect(status().isNotFound())
        .andExpect(content().string("The data source \"invalid-source\" does not exist."));

    verify(logService, times(3)).logRequest(any(HttpServletRequest.class));
    verify(logService, times(3)).logRequestComplete(any(BigInteger.class), anyInt());

    mvc.perform(get("/linked-data/invalid-source/valid-id/navigation/UM/valid-source?distance=1"))
        .andExpect(status().isNotFound())
        .andExpect(content().string("The feature source \"invalid-source\" does not exist."));

    verify(logService, times(4)).logRequest(any(HttpServletRequest.class));
    verify(logService, times(4)).logRequestComplete(any(BigInteger.class), anyInt());

    mvc.perform(get("/linked-data/valid-source/invalid-id/navigation/UM/valid-source?distance=1"))
        .andExpect(status().isNotFound())
        .andExpect(
            content()
                .string(
                    "The feature ID \"invalid-id\" does not exist in feature source \"source\"."));

    verify(logService, times(5)).logRequest(any(HttpServletRequest.class));
    verify(logService, times(5)).logRequestComplete(any(BigInteger.class), anyInt());
  }

  @Test
  public void getNavigationTest() throws Exception {
    mvc.perform(get("/linked-data/valid-source/valid-id/navigation/WRONG"))
        .andExpect(status().isBadRequest());

    mvc.perform(get("/linked-data/valid-source/valid-id/navigation/UM")).andExpect(status().isOk());

    // logging gets called twice for this request because it calls another controller function
    verify(logService, times(2)).logRequest(any(HttpServletRequest.class));
    verify(logService, times(2))
        .logRequestComplete(any(BigInteger.class), eq(HttpStatus.OK.value()));

    mvc.perform(get("/linked-data/invalid-source/valid-id/navigation/UM"))
        .andExpect(status().isNotFound())
        .andExpect(content().string("The feature source \"invalid-source\" does not exist."));

    verify(logService, times(3)).logRequest(any(HttpServletRequest.class));
    verify(logService, times(3)).logRequestComplete(any(BigInteger.class), anyInt());

    mvc.perform(get("/linked-data/valid-source/invalid-id/navigation/UM"))
        .andExpect(status().isNotFound())
        .andExpect(
            content()
                .string(
                    "The feature ID \"invalid-id\" does not exist in feature source \"source\"."));

    verify(logService, times(4)).logRequest(any(HttpServletRequest.class));
    verify(logService, times(4)).logRequestComplete(any(BigInteger.class), anyInt());
  }

  @Test
  public void getNavigationFlowlinesTest() throws Exception {
    mvc.perform(get("/linked-data/valid-source/valid-id/navigation/WRONG/flowlines?distance=1"))
        .andExpect(status().isBadRequest());

    mvc.perform(get("/linked-data/valid-source/valid-id/navigation/WRONG/flowlines?distance=-1"))
        .andExpect(status().isBadRequest());

    mvc.perform(get("/linked-data/valid-source/valid-id/navigation/UM/flowlines?distance=1"))
        .andExpect(status().isOk());

    verify(logService, times(1)).logRequest(any(HttpServletRequest.class));
    verify(logService, times(1))
        .logRequestComplete(any(BigInteger.class), eq(HttpStatus.OK.value()));

    mvc.perform(
            get(
                "/linked-data/valid-source/valid-id/navigation/DM/flowlines?distance=1&trimStart=invalid"))
        .andExpect(status().isBadRequest());

    mvc.perform(
            get(
                "/linked-data/valid-source/valid-id/navigation/DM/flowlines?distance=1&trimStart=false"))
        .andExpect(status().isOk());

    verify(logService, times(2)).logRequest(any(HttpServletRequest.class));
    verify(logService, times(2))
        .logRequestComplete(any(BigInteger.class), eq(HttpStatus.OK.value()));

    mvc.perform(
            get(
                "/linked-data/valid-source/valid-id/navigation/DD/flowlines?distance=1&trimStart=true&trimTolerance=0.1"))
        .andExpect(status().isOk());

    verify(logService, times(3)).logRequest(any(HttpServletRequest.class));
    verify(logService, times(3))
        .logRequestComplete(any(BigInteger.class), eq(HttpStatus.OK.value()));

    mvc.perform(
            get(
                "/linked-data/valid-source/valid-id/navigation/UM/flowlines?distance=1&stopComid=-1"))
        .andExpect(status().isBadRequest());

    mvc.perform(
            get(
                "/linked-data/valid-source/valid-id/navigation/UM/flowlines?distance=1&stopComid=12345"))
        .andExpect(status().isOk());

    verify(logService, times(4)).logRequest(any(HttpServletRequest.class));
    verify(logService, times(4))
        .logRequestComplete(any(BigInteger.class), eq(HttpStatus.OK.value()));

    mvc.perform(
            get(
                "/linked-data/valid-source/valid-id/navigation/UM/flowlines?distance=1&legacy=true"))
        .andExpect(status().isOk());

    verify(logService, times(5)).logRequest(any(HttpServletRequest.class));
    verify(logService, times(5))
        .logRequestComplete(any(BigInteger.class), eq(HttpStatus.OK.value()));

    mvc.perform(get("/linked-data/invalid-source/valid-id/navigation/UM/flowlines?distance=1"))
        .andExpect(status().isNotFound())
        .andExpect(content().string("The feature source \"invalid-source\" does not exist."));

    verify(logService, times(6)).logRequest(any(HttpServletRequest.class));
    verify(logService, times(6)).logRequestComplete(any(BigInteger.class), anyInt());

    mvc.perform(get("/linked-data/valid-source/invalid-id/navigation/UM/flowlines?distance=1"))
        .andExpect(status().isNotFound())
        .andExpect(
            content()
                .string(
                    "The feature ID \"invalid-id\" does not exist in feature source \"source\"."));

    verify(logService, times(7)).logRequest(any(HttpServletRequest.class));
    verify(logService, times(7)).logRequestComplete(any(BigInteger.class), anyInt());
  }

  public static List<Map<String, Object>> getTestList() {
    List<Map<String, Object>> rtn = new ArrayList<>();
    Map<String, Object> entry = new HashMap<>();
    entry.put("source", "source");
    entry.put("sourceName", "source name");
    entry.put("features", "features-url");
    rtn.add(entry);
    return rtn;
  }
}
