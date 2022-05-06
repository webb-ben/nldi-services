package gov.usgs.owi.nldi.controllers;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import gov.usgs.owi.nldi.transform.FeatureTransformer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DatabaseSetup("classpath:/testData/deprecated/networkController/DataSource.xml")
// This test class contains tests for the deprecated "navigate" endpoints.  Don't add
// new tests here and delete this class when we drop support for those endpoints.
// The new tests that are tied to the new "navigation" endpoints are in
// NetworkControllerDataSourceIT
public class DeprecatedNetworkControllerDataSourceIT extends BaseControllerIT {
  private final String RESULT_FOLDER = "deprecated/networkController/dataSource/";

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
        "/linked-data/comid/13293474/navigate/UT/wqp",
        HttpStatus.OK.value(),
        FeatureTransformer.FEATURE_COUNT_HEADER,
        "22",
        BaseController.MIME_TYPE_GEOJSON,
        getCompareFile(RESULT_FOLDER, "getComidUtTest.json"),
        true,
        false);
  }

  @Test
  public void getComidUtDistanceTest() throws Exception {
    assertEntity(
        restTemplate,
        "/linked-data/comid/13297246/navigate/UT/wqp?distance=2",
        HttpStatus.OK.value(),
        FeatureTransformer.FEATURE_COUNT_HEADER,
        "3",
        BaseController.MIME_TYPE_GEOJSON,
        getCompareFile(RESULT_FOLDER, "getComidUtDistanceTest.json"),
        true,
        false);
  }

  @Test
  public void getComidUtDistanceTestEmpty() throws Exception {
    assertEntity(
        restTemplate,
        "/linked-data/comid/13297246/navigate/UT/wqp?distance=",
        HttpStatus.OK.value(),
        FeatureTransformer.FEATURE_COUNT_HEADER,
        "3",
        BaseController.MIME_TYPE_GEOJSON,
        getCompareFile(RESULT_FOLDER, "getComidUtDistanceTestEmpty.json"),
        true,
        false);
  }

  @Test
  public void getComidUtDistanceTestAboveMax() throws Exception {
    assertEntity(
        restTemplate,
        "/linked-data/comid/13297246/navigate/UT/wqp?distance=10000",
        HttpStatus.BAD_REQUEST.value(),
        null,
        null,
        null,
        "getFeaturesDeprecated.distance: distance must be between 1 and 9999 kilometers",
        false,
        false);
  }

  @Test
  public void getComidUtDistanceTestBelowMin() throws Exception {
    assertEntity(
        restTemplate,
        "/linked-data/comid/13297246/navigate/UT/wqp?distance=-1",
        HttpStatus.BAD_REQUEST.value(),
        null,
        null,
        null,
        "getFeaturesDeprecated.distance: distance must be between 1 and 9999 kilometers",
        false,
        false);
  }

  // UM Testing
  @Test
  public void getComidUmTest() throws Exception {
    assertEntity(
        restTemplate,
        "/linked-data/comid/13293474/navigate/UM/wqp",
        HttpStatus.OK.value(),
        FeatureTransformer.FEATURE_COUNT_HEADER,
        "16",
        BaseController.MIME_TYPE_GEOJSON,
        getCompareFile(RESULT_FOLDER, "getComidUmTest.json"),
        true,
        false);
  }

  @Test
  public void getComidUmDistanceTest() throws Exception {
    assertEntity(
        restTemplate,
        "/linked-data/comid/13297246/navigate/UM/wqp?distance=10",
        HttpStatus.OK.value(),
        FeatureTransformer.FEATURE_COUNT_HEADER,
        "3",
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
        "/linked-data/comid/13296790/navigate/DM/wqp",
        HttpStatus.OK.value(),
        FeatureTransformer.FEATURE_COUNT_HEADER,
        "1",
        BaseController.MIME_TYPE_GEOJSON,
        getCompareFile(RESULT_FOLDER, "getComidDmTest.json"),
        true,
        false);
  }

  @Test
  public void getComidDmDistanceTest() throws Exception {
    assertEntity(
        restTemplate,
        "/linked-data/comid/13293474/navigate/DM/wqp?distance=10",
        HttpStatus.OK.value(),
        FeatureTransformer.FEATURE_COUNT_HEADER,
        "10",
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
        "/linked-data/comid/13294310/navigate/DD/wqp",
        HttpStatus.OK.value(),
        FeatureTransformer.FEATURE_COUNT_HEADER,
        "34",
        BaseController.MIME_TYPE_GEOJSON,
        getCompareFile(RESULT_FOLDER, "getComidDdTest.json"),
        true,
        false);
  }

  @Test
  public void getComidDdDistanceTest() throws Exception {
    assertEntity(
        restTemplate,
        "/linked-data/comid/13294310/navigate/DD/wqp?distance=1",
        HttpStatus.OK.value(),
        FeatureTransformer.FEATURE_COUNT_HEADER,
        "1",
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
        "/linked-data/comid/13297246/navigate/PP/wqp?stopComid=13297198",
        HttpStatus.BAD_REQUEST.value(),
        null,
        null,
        null,
        null,
        true,
        true);
  }

  @Test
  public void getComidPpStopComidTest() throws Exception {
    assertEntity(
        restTemplate,
        "/linked-data/comid/13297198/navigate/PP/wqp?stopComid=13297246",
        HttpStatus.OK.value(),
        FeatureTransformer.FEATURE_COUNT_HEADER,
        "11",
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
        "/linked-data/comid/13297198/navigate/XX/wqp",
        HttpStatus.BAD_REQUEST.value(),
        null,
        null,
        null,
        "getFeaturesDeprecated.navigationMode: must match \"DD|DM|PP|UT|UM\"",
        false,
        false);
  }

  @Test
  public void getBasinTest() throws Exception {
    assertEntity(
        restTemplate,
        "/linked-data/comid/13297246/navigate/UT/basin",
        HttpStatus.OK.value(),
        null,
        null,
        BaseController.MIME_TYPE_GEOJSON,
        null,
        false,
        false);
  }
}
