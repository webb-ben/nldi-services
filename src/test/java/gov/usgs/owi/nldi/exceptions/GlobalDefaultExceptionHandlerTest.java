package gov.usgs.owi.nldi.exceptions;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import gov.usgs.owi.nldi.controllers.LinkedDataController;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

@WebMvcTest(
    value = LinkedDataController.class,
    properties = {"springFrameworkLogLevel=INFO", "serverPort=8080"})
public class GlobalDefaultExceptionHandlerTest {

  @MockBean LinkedDataController controller;
  @Autowired private MockMvc mvc;

  @Test
  public void handleUncaughtExceptionTest() throws Exception {
    doThrow(Exception.class)
        .when(controller)
        .getNavigationFlowlines(
            any(), any(), any(), any(), any(), any(), any(), any(), any(), any());
    mvc.perform(get("/linked-data/wqp/USGS-12345/navigation/UM/flowlines?distance=1"))
        .andExpect(status().isInternalServerError())
        .andExpect(
            content()
                .string(
                    containsString("Something bad happened. Contact us with Reference Number:")));
  }

  @Test
  public void handleMissingServletRequestParameterTest() throws Exception {
    // missing distance parameter
    mvc.perform(get("/linked-data/wqp/USGS-12345/navigation/UM/flowlines"))
        .andExpect(status().isBadRequest())
        .andExpect(
            content()
                .string(
                    Matchers.containsString(
                        "Required String parameter 'distance' is not present")));
  }

  @Test
  public void handleHttpMessageNotReadableTest() throws Exception {
    String errorMessage = "This is a multiline response.\nThis line should not be shown";
    String responseMessage = errorMessage.substring(0, errorMessage.indexOf("\n"));
    doThrow(new HttpMessageNotReadableException(errorMessage))
        .when(controller)
        .getRegisteredFeature(any(), any(), any(), any());
    mvc.perform(get("/linked-data/test/error"))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(Matchers.containsString(responseMessage)));
  }

  @Test
  public void handleFeatureIdNotFoundExceptionTest() throws Exception {
    String featureId = "id";
    String featureSource = "source";
    String responseMessage =
        String.format(
            "The feature ID '%s' does not exist in feature source '%s'.", featureId, featureSource);
    doThrow(new FeatureIdNotFoundException(featureSource, featureId))
        .when(controller)
        .getRegisteredFeature(any(), any(), any(), any());
    mvc.perform(get("/linked-data/test/error"))
        .andExpect(status().isNotFound())
        .andExpect(content().string(Matchers.containsString(responseMessage)));
  }

  @Test
  public void handleBadRequestExceptionTest() throws Exception {
    doThrow(new ResponseStatusException(HttpStatus.ACCEPTED))
        .when(controller)
        .getRegisteredFeature(any(), any(), any(), any());
    mvc.perform(get("/linked-data/test/test")).andExpect(status().isBadRequest());
  }
}
