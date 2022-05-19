package gov.usgs.owi.nldi.exceptions;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import gov.usgs.owi.nldi.controllers.LinkedDataController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.server.ResponseStatusException;

@WebMvcTest(
    value = LinkedDataController.class,
    properties = {"springFrameworkLogLevel=INFO", "serverPort=8080"})
public class GlobalDefaultExceptionHandlerTest {

  @MockBean LinkedDataController controller;
  @Autowired private MockMvc mvc;

  @Test
  public void handleUncaughtExceptionTest() throws Exception {
    doThrow(new Exception()).when(controller).getRegisteredFeature(any(), any(), any(), any());
    mvc.perform(get("/linked-data/test/error"))
        .andExpect(status().isInternalServerError())
        .andExpect(
            content()
                .string(
                    containsString("Something bad happened. Contact us with Reference Number:")));
  }

  @Test
  public void handleMissingServletRequestParameterTest() throws Exception {
    String paramType = "String";
    String paramValue = "value";
    String errorMessage =
        String.format("Required %s parameter '%s' is not present", paramType, paramValue);
    doThrow(new MissingServletRequestParameterException(paramValue, paramType))
        .when(controller)
        .getRegisteredFeature(any(), any(), any(), any());
    mvc.perform(get("/linked-data/test/error"))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(errorMessage));
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
        .andExpect(content().string(responseMessage));
  }

  @Test
  public void handleFeatureIdNotFoundExceptionTest() throws Exception {
    String featureId = "id";
    String featureSource = "source";
    String responseMessage =
        String.format(
            "The feature ID \"%s\" does not exist in feature source \"%s\".",
            featureId, featureSource);
    doThrow(new FeatureIdNotFoundException(featureSource, featureId))
        .when(controller)
        .getRegisteredFeature(any(), any(), any(), any());
    mvc.perform(get("/linked-data/test/error"))
        .andExpect(status().isNotFound())
        .andExpect(content().string(responseMessage));
  }

  @Test
  public void handleBadRequestExceptionTest() throws Exception {
    doThrow(new ResponseStatusException(HttpStatus.ACCEPTED))
        .when(controller)
        .getRegisteredFeature(any(), any(), any(), any());
    mvc.perform(get("/linked-data/test/test")).andExpect(status().isBadRequest());
  }
}
