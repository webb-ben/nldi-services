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
@DatabaseSetup("classpath:/testData/linkedDataController/Other.xml")
public class LinkedDataControllerOtherIT extends BaseControllerIT {
  private final String RESULT_FOLDER = "linkedDataController/other/";

  @LocalServerPort private int port;

  @Autowired private TestRestTemplate restTemplate;

  @BeforeEach
  public void setUp() {
    urlRoot = "http://localhost:" + port + context;
  }

  @Test
  public void getCharacteristicDataTest() throws Exception {
    assertEntity(
        restTemplate,
        "/linked-data/comid/13302592/tot",
        HttpStatus.OK.value(),
        null,
        getCompareFile(RESULT_FOLDER, "getCharacteristicDataTest.json"),
        true,
        false);
  }

  @Test
  public void getCharacteristicDataMissingTest() throws Exception {
    assertEntity(
        restTemplate,
        "/linked-data/comid/133999999/tot",
        HttpStatus.NOT_FOUND.value(),
        null,
        null,
        true,
        true);
  }

  @Test
  public void getCharacteristicDataFilteredTest() throws Exception {
    assertEntity(
        restTemplate,
        "/linked-data/comid/13302592/tot?characteristicId=TOT_N97&characteristicId=TOT_ET",
        HttpStatus.OK.value(),
        null,
        getCompareFile(RESULT_FOLDER, "getCharacteristicDataFilteredTest.json"),
        true,
        false);
  }

  @Test
  public void getBasinTest() throws Exception {
    assertEntity(
        restTemplate,
        "/linked-data/comid/13302592/basin",
        HttpStatus.OK.value(),
        BaseController.MIME_TYPE_GEOJSON,
        getCompareFile(RESULT_FOLDER, "getBasinTest.json"),
        true,
        false);
  }

  @Test
  public void getBasinMissingTest() throws Exception {
    assertEntity(
        restTemplate,
        "/linked-data/comid/1330259299/basin",
        HttpStatus.NOT_FOUND.value(),
        null,
        null,
        true,
        true);
  }

  // DataSources Testing
  @Test
  public void getDataSourcesTest() throws Exception {
    assertEntity(
        restTemplate,
        "/linked-data",
        HttpStatus.OK.value(),
        MediaType.APPLICATION_JSON_VALUE,
        getCompareFile(RESULT_FOLDER, "getDataSourcesTest.json"),
        true,
        false);
  }

  // Features Testing
  @Test
  public void getFeaturesTest() throws Exception {
    assertEntity(
        restTemplate,
        "/linked-data/wqp",
        HttpStatus.OK.value(),
        null,
        getCompareFile(RESULT_FOLDER, "getFeaturesTest.json"),
        true,
        false);
  }

  @Test
  public void getFeaturesTestInvalid() throws Exception {
    assertEntity(
        restTemplate,
        "/linked-data/wqx",
        HttpStatus.NOT_FOUND.value(),
        null,
        "{\"description\":\"The feature source 'wqx' does not exist.\",\"type\":\"error\"}",
        true,
        false);
  }

  // Object Testing Catchment
  @Test
  public void getComidTest() throws Exception {
    assertEntity(
        restTemplate,
        "/linked-data/comid/13294288",
        HttpStatus.OK.value(),
        BaseController.MIME_TYPE_GEOJSON,
        getCompareFile(RESULT_FOLDER, "getComidTest.json"),
        true,
        false);
  }

  // Linked Object Testing WQP
  @Test
  public void getWqpTest() throws Exception {
    assertEntity(
        restTemplate,
        "/linked-data/wqp/USGS-05427880",
        HttpStatus.OK.value(),
        BaseController.MIME_TYPE_GEOJSON,
        getCompareFile(RESULT_FOLDER, "getWqpTest.json"),
        true,
        false);
  }

  @Test
  public void getWqpJsonLdTest() throws Exception {
    assertEntity(
            restTemplate,
            "/linked-data/wqp/USGS-05427880?f=jsonld",
            HttpStatus.OK.value(),
            BaseController.MIME_TYPE_JSONLD,
            getCompareFile(RESULT_FOLDER, "getWqpJsonLdTest.json"),
            true,
            false);
  }

  // Linked Object Testing huc12pp
  @Test
  public void getHuc12ppTest() throws Exception {
    assertEntity(
        restTemplate,
        "/linked-data/huc12pp/070900020604",
        HttpStatus.OK.value(),
        BaseController.MIME_TYPE_GEOJSON,
        getCompareFile(RESULT_FOLDER, "getHuc12ppTest.json"),
        true,
        false);
  }

  // Navigation Types Testing
  @Test
  public void getNavigationTypesTest() throws Exception {
    assertEntity(
        restTemplate,
        "/linked-data/wqp/USGS-05427880/navigation",
        HttpStatus.OK.value(),
        MediaType.APPLICATION_JSON_VALUE,
        getCompareFile(RESULT_FOLDER, "getNavigationTypesTest.json"),
        true,
        false);
  }

  @Test
  public void getNavigationTypesNotFoundTest() throws Exception {
    assertEntity(
        restTemplate,
        "/linked-data/wqx/USGS-05427880/navigation",
        HttpStatus.NOT_FOUND.value(),
        MediaType.APPLICATION_JSON_VALUE,
        null,
        true,
        false);

    assertEntity(
        restTemplate,
        "/linked-data/wqp/USGX-05427880/navigation",
        HttpStatus.NOT_FOUND.value(),
        MediaType.APPLICATION_JSON_VALUE,
        null,
        true,
        false);
  }

  // Navigation Types Testing
  @Test
  public void getNavigationOptionsTest() throws Exception {
    assertEntity(
        restTemplate,
        "/linked-data/wqp/USGS-05427880/navigation/UT?f=json",
        HttpStatus.OK.value(),
        MediaType.APPLICATION_JSON_VALUE,
        getCompareFile(RESULT_FOLDER, "getNavigationOptionsTest.json"),
        true,
        false);
  }

  @Test
  public void getNavigationOptionsTestBadRequest() throws Exception {
    assertEntity(
        restTemplate,
        "/linked-data/wqp/USGS-05427880/navigation/XX",
        HttpStatus.BAD_REQUEST.value(),
        null,
        null,
        false,
        false);
  }
}
