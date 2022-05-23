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
@DatabaseSetup("classpath:/testData/networkController/DataSource.xml")
public class NetworkControllerDataSourceIT extends BaseControllerIT {
  private final String RESULT_FOLDER = "networkController/dataSource/";

  @LocalServerPort private int port;

  @Autowired private TestRestTemplate restTemplate;

  @BeforeEach
  public void setUp() {
    urlRoot = "http://localhost:" + port + context;
  }

  // UT Testing
  @Test
  public void getComidUtTest() throws Exception {
    assertEntity(
        restTemplate,
        "/linked-data/comid/13293474/navigation/UT/wqp?distance=9999",
        HttpStatus.OK.value(),
        BaseController.MIME_TYPE_GEOJSON,
        getCompareFile(RESULT_FOLDER, "getComidUtTest.json"),
        true,
        false);
  }

  @Test
  public void getComidUtDistanceTest() throws Exception {
    assertEntity(
        restTemplate,
        "/linked-data/comid/13297246/navigation/UT/wqp?distance=2",
        HttpStatus.OK.value(),
        BaseController.MIME_TYPE_GEOJSON,
        getCompareFile(RESULT_FOLDER, "getComidUtDistanceTest.json"),
        true,
        false);
  }

  @Test
  public void getComidUtDistanceTestEmpty() throws Exception {
    assertEntity(
        restTemplate,
        "/linked-data/comid/13297246/navigation/UT/wqp?distance=",
        HttpStatus.OK.value(),
        BaseController.MIME_TYPE_GEOJSON,
        getCompareFile(RESULT_FOLDER, "getComidUtDistanceTestEmpty.json"),
        true,
        false);
  }

  @Test
  public void getComidUtDistanceTestAboveMax() throws Exception {
    assertEntity(
        restTemplate,
        "/linked-data/comid/13297246/navigation/UT/wqp?distance=10000",
        HttpStatus.BAD_REQUEST.value(),
        null,
        "getFeatures.distance: distance must be between 1 and 9999 kilometers",
        false,
        false);
  }

  @Test
  public void getComidUtDistanceTestBelowMin() throws Exception {
    assertEntity(
        restTemplate,
        "/linked-data/comid/13297246/navigation/UT/wqp?distance=-1",
        HttpStatus.BAD_REQUEST.value(),
        null,
        "getFeatures.distance: distance must be between 1 and 9999 kilometers",
        false,
        false);
  }

  // UM Testing
  @Test
  public void getComidUmTest() throws Exception {
    assertEntity(
        restTemplate,
        "/linked-data/comid/13293474/navigation/UM/wqp?distance=9999",
        HttpStatus.OK.value(),
        BaseController.MIME_TYPE_GEOJSON,
        getCompareFile(RESULT_FOLDER, "getComidUmTest.json"),
        true,
        false);
  }

  @Test
  public void getComidUmDistanceTest() throws Exception {
    assertEntity(
        restTemplate,
        "/linked-data/comid/13297246/navigation/UM/wqp?distance=10",
        HttpStatus.OK.value(),
        BaseController.MIME_TYPE_GEOJSON,
        getCompareFile(RESULT_FOLDER, "getComidUmDistanceTest.json"),
        true,
        false);
  }

  // DM Testing
  @Test
  public void getComidDmTest() throws Exception {
    assertEntity(
        restTemplate,
        "/linked-data/comid/13296790/navigation/DM/wqp?distance=9999",
        HttpStatus.OK.value(),
        BaseController.MIME_TYPE_GEOJSON,
        getCompareFile(RESULT_FOLDER, "getComidDmTest.json"),
        true,
        false);
  }

  @Test
  public void getComidDmDistanceTest() throws Exception {
    assertEntity(
        restTemplate,
        "/linked-data/comid/13293474/navigation/DM/wqp?distance=10",
        HttpStatus.OK.value(),
        BaseController.MIME_TYPE_GEOJSON,
        getCompareFile(RESULT_FOLDER, "getComidDmDistanceTest.json"),
        true,
        false);
  }

  // DD Testing
  @Test
  public void getComidDdTest() throws Exception {
    assertEntity(
        restTemplate,
        "/linked-data/comid/13294310/navigation/DD/wqp?distance=9999",
        HttpStatus.OK.value(),
        BaseController.MIME_TYPE_GEOJSON,
        getCompareFile(RESULT_FOLDER, "getComidDdTest.json"),
        true,
        false);
  }

  @Test
  public void getComidDdDistanceTest() throws Exception {
    assertEntity(
        restTemplate,
        "/linked-data/comid/13294310/navigation/DD/wqp?distance=1",
        HttpStatus.OK.value(),
        BaseController.MIME_TYPE_GEOJSON,
        getCompareFile(RESULT_FOLDER, "getComidDdDistanceTest.json"),
        true,
        false);
  }

  // PP Testing
  @Test
  public void getComidPpStopComidInvalidTest() throws Exception {
    assertEntity(
        restTemplate,
        "/linked-data/comid/13297246/navigation/PP/wqp?distance=9999&stopComid=13297198",
        HttpStatus.BAD_REQUEST.value(),
        null,
        "400 BAD_REQUEST \"The stopComid must be downstream of the start comid.\"",
        false,
        true);
  }

  @Test
  public void getComidPpStopComidTest() throws Exception {
    assertEntity(
        restTemplate,
        "/linked-data/comid/13297198/navigation/PP/wqp?distance=9999&stopComid=13297246",
        HttpStatus.OK.value(),
        BaseController.MIME_TYPE_GEOJSON,
        getCompareFile(RESULT_FOLDER, "getComidPpStopComidTest.json"),
        true,
        false);
  }

  // Parameter Error Testing
  @Test
  public void badNavigationModeTest() throws Exception {
    assertEntity(
        restTemplate,
        "/linked-data/comid/13297198/navigation/XX/wqp?distance=9999",
        HttpStatus.BAD_REQUEST.value(),
        null,
        "getFeatures.navigationMode: must match \"DD|DM|PP|UT|UM\"",
        false,
        false);
  }

  @Test
  public void getBasinTest() throws Exception {
    assertEntity(
        restTemplate,
        "/linked-data/comid/13297246/navigation/UT/invalid?distance=9999",
        HttpStatus.NOT_FOUND.value(),
        null,
        null,
        false,
        false);
  }

  // Navigation Types Testing
  @Test
  public void getNavigationTypesTest() throws Exception {
    assertEntity(
        restTemplate,
        "/linked-data/comid/13293474/navigation",
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
        "/linked-data/comid/123/navigation",
        HttpStatus.NOT_FOUND.value(),
        MediaType.APPLICATION_JSON_VALUE,
        null,
        true,
        false);
  }
}
