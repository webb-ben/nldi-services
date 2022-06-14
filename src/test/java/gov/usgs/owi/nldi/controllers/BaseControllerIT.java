package gov.usgs.owi.nldi.controllers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static uk.co.datumedge.hamcrest.json.SameJSONAs.sameJSONArrayAs;
import static uk.co.datumedge.hamcrest.json.SameJSONAs.sameJSONObjectAs;

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;
import gov.usgs.owi.nldi.ColumnSensingFlatXMLDataSetLoader;
import java.io.IOException;
import java.util.Collections;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;

@ActiveProfiles("it")
@TestExecutionListeners({
  DependencyInjectionTestExecutionListener.class,
  TransactionDbUnitTestExecutionListener.class
})
@DbUnitConfiguration(dataSetLoader = ColumnSensingFlatXMLDataSetLoader.class)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
@DatabaseTearDown("classpath:/testData/Cleanup.xml")
public abstract class BaseControllerIT {
  @Value("${serverContextPath}")
  protected String context;

  protected String urlRoot;

  public String getCompareFile(String folder, String file) throws IOException {
    return new String(
        FileCopyUtils.copyToByteArray(
            new ClassPathResource("testResult/" + folder + file).getInputStream()));
  }

  protected String assertEntity(
      TestRestTemplate restTemplate,
      String url,
      int httpStatus,
      String contentType,
      String expectedBody,
      boolean isJson,
      boolean extraFieldsAllowed)
      throws JSONException {
    HttpHeaders headers = new HttpHeaders();
    if (contentType != null) {
      headers.setAccept(Collections.singletonList(MediaType.valueOf(contentType)));
    } else {
      headers.setAccept(Collections.singletonList(MediaType.ALL));
    }
    HttpEntity<String> requestEntity = new HttpEntity(headers);
    ResponseEntity<String> entity =
        restTemplate.exchange(urlRoot + url, HttpMethod.GET, requestEntity, String.class);

    assertEquals(httpStatus, entity.getStatusCodeValue());
    if (null != contentType) {
      assertEquals(contentType, entity.getHeaders().getContentType().toString());
    }
    assertTrue(entity.getHeaders().get(HttpHeaders.SET_COOKIE).get(0).startsWith("XSRF-TOKEN"));
    if (null != expectedBody) {
      assertBody(entity, expectedBody, isJson, extraFieldsAllowed);
    }
    return entity.getBody();
  }

  protected void assertBody(
      ResponseEntity<String> entity,
      String expectedBody,
      boolean isJson,
      boolean extraFieldsAllowed)
      throws JSONException {
    if (isJson) {
      boolean isJsonArray = false;
      JSONObject objectBody = null;
      JSONObject objectExpectedBody = null;
      JSONArray arrayBody = null;
      JSONArray arrayExpectedBody = null;
      try {
        objectBody = new JSONObject(entity.getBody());
        objectExpectedBody = new JSONObject(expectedBody);
      } catch (JSONException exception) {
        // several requests return JSON arrays rather than objects
        // this allows the arrays to be compared using the same syntax
        if (exception
            .getMessage()
            .contains("org.json.JSONArray cannot be converted to JSONObject")) {
          isJsonArray = true;
          arrayBody = new JSONArray(entity.getBody());
          arrayExpectedBody = new JSONArray(expectedBody);
        } else {
          throw exception;
        }
      }

      if (extraFieldsAllowed && !isJsonArray) {
        assertThat(
            objectBody,
            sameJSONObjectAs(objectExpectedBody)
                .allowingAnyArrayOrdering()
                .allowingExtraUnexpectedFields());
      } else if (!isJsonArray) {
        assertThat(objectBody, sameJSONObjectAs(objectExpectedBody).allowingAnyArrayOrdering());
      } else if (extraFieldsAllowed && isJsonArray) {
        assertThat(
            arrayBody,
            sameJSONArrayAs(arrayExpectedBody)
                .allowingAnyArrayOrdering()
                .allowingExtraUnexpectedFields());
      } else {
        assertThat(arrayBody, sameJSONArrayAs(arrayExpectedBody).allowingAnyArrayOrdering());
      }
    } else {
      assertEquals(expectedBody, entity.getBody());
    }
  }
}
