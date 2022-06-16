package gov.usgs.owi.nldi.controllers;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DatabaseSetup("classpath:/testData/linkedDataController/DataSource.xml")
public class LinkedDataControllerDataSourceIT extends BaseControllerIT {
  public final String RESULT_FOLDER = "linkedDataController/dataSource/";

  @LocalServerPort private int port;

  @Autowired private TestRestTemplate restTemplate;

  @BeforeEach
  public void setUp() {
    urlRoot = "http://localhost:" + port + context;
  }

  // Navigation Within Datasource Testing
  @Test
  public void getWqpUtTest() throws Exception {
    assertEntity(
        restTemplate,
        "/linked-data/wqp/USGS-05427880/navigation/UT/wqp?distance=9999",
        HttpStatus.OK.value(),
        BaseController.MIME_TYPE_GEOJSON,
        getCompareFile(RESULT_FOLDER, "getWqpUtTest.json"),
        true,
        false);
  }

  @Test
  public void getWqpJsonLdTest() throws Exception {
    assertEntity(
            restTemplate,
            "/linked-data/wqp/USGS-05427880/navigation/UM/wqp?distance=10",
            HttpStatus.OK.value(),
            BaseController.MIME_TYPE_JSONLD,
            getCompareFile(RESULT_FOLDER, "getWqpJsonLdTest.json"),
            true,
            false);
  }

  @Test
  public void getWqpUtTestDistance() throws Exception {
    assertEntity(
        restTemplate,
        "/linked-data/wqp/USGS-05427880/navigation/UT/wqp?distance=1",
        HttpStatus.OK.value(),
        BaseController.MIME_TYPE_GEOJSON,
        getCompareFile(RESULT_FOLDER, "getWqpUtTestDistance.json"),
        true,
        false);
  }

  @Test
  public void getWqpUtTestDistanceAboveMax() throws Exception {
    assertEntity(
        restTemplate,
        "/linked-data/wqp/USGS-05427880/navigation/UT/wqp?distance=10000",
        HttpStatus.BAD_REQUEST.value(),
        MediaType.APPLICATION_JSON_VALUE,
        "{\"description\":\"getFeatures.distance: distance must be between 1 and 9999"
            + " kilometers\",\"type\":\"error\"}",
        true,
        false);
  }

  @Test
  public void getWqpUtTestDistanceBelowMin() throws Exception {
    assertEntity(
        restTemplate,
        "/linked-data/wqp/USGS-05427880/navigation/UT/wqp?distance=-1",
        HttpStatus.BAD_REQUEST.value(),
        MediaType.APPLICATION_JSON_VALUE,
        "{\"description\":\"getFeatures.distance: distance must be between 1 and 9999"
            + " kilometers\",\"type\":\"error\"}",
        true,
        false);
  }

  // Navigation Different Datasource Testing
  @Test
  public void getWqpDmTest() throws Exception {
    assertEntity(
        restTemplate,
        "/linked-data/wqp/USGS-05427880/navigation/DM/huc12pp?distance=9999",
        HttpStatus.OK.value(),
        BaseController.MIME_TYPE_GEOJSON,
        getCompareFile(RESULT_FOLDER, "getWqpDmTest.json"),
        true,
        false);
  }

  @Test
  public void badInputTest() throws Exception {
    assertEntity(
        restTemplate,
        "/linked-data/wqx/USGS-05427880/navigation/DM/huc12pp?distance=9999",
        HttpStatus.NOT_FOUND.value(),
        null,
        null,
        true,
        false);
  }

  // Parameter Error Testing
  @Test
  public void badNavigationModeTest() throws Exception {
    assertEntity(
        restTemplate,
        "/linked-data/wqp/USGS-05427880/navigation/XX/huc12pp?distance=9999",
        HttpStatus.BAD_REQUEST.value(),
        MediaType.APPLICATION_JSON_VALUE,
        "{\"description\":\"getFeatures.navigationMode: must match"
            + " 'DD|DM|PP|UT|UM'\",\"type\":\"error\"}",
        true,
        false);
  }
}
