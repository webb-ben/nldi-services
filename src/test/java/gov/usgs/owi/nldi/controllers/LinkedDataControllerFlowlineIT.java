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
@DatabaseSetup("classpath:/testData/linkedDataController/Flowline.xml")
public class LinkedDataControllerFlowlineIT extends BaseControllerIT {
  private final String RESULT_FOLDER = "linkedDataController/flowline/";

  @LocalServerPort private int port;

  @Autowired private TestRestTemplate restTemplate;

  @BeforeEach
  public void setUp() {
    urlRoot = "http://localhost:" + port + context;
  }

  @Test
  public void getWqpUMTest() throws Exception {
    assertEntity(
        restTemplate,
        "/linked-data/wqp/USGS-05427880/navigation/UM/flowlines?distance=9999",
        HttpStatus.OK.value(),
        BaseController.MIME_TYPE_GEOJSON,
        getCompareFile(RESULT_FOLDER, "getWqpUMTest.json"),
        true,
        false);
  }

  @Test
  public void getHuc12ppDM10Test() throws Exception {
    assertEntity(
        restTemplate,
        "/linked-data/huc12pp/070900020601/navigation/DM/flowlines?distance=10",
        HttpStatus.OK.value(),
        BaseController.MIME_TYPE_GEOJSON,
        getCompareFile(RESULT_FOLDER, "getHuc12ppDM10Test.json"),
        true,
        false);
  }

  @Test
  public void getHuc12ppDM10000TestDistanceAboveMax() throws Exception {
    assertEntity(
        restTemplate,
        "/linked-data/huc12pp/070900020601/navigation/DM/flowlines?distance=10000",
        HttpStatus.BAD_REQUEST.value(),
            MediaType.APPLICATION_JSON_VALUE,
        "{\"description\":\"getNavigationFlowlines.distance: distance must be between 1 and 9999 kilometers\",\"type\":\"error\"}",
        true,
        false);
  }

  @Test
  public void getHuc12ppDM0TestDistanceBelowMin() throws Exception {
    assertEntity(
        restTemplate,
        "/linked-data/huc12pp/070900020601/navigation/DM/flowlines/?distance=-1",
        HttpStatus.BAD_REQUEST.value(),
        MediaType.APPLICATION_JSON_VALUE,
        "{\"description\":\"getNavigationFlowlines.distance: distance must be between 1 and 9999 kilometers\",\"type\":\"error\"}",
        true,
        false);
  }

  @Test
  public void getHuc12ppDMTestEmptyDistance() throws Exception {
    assertEntity(
        restTemplate,
        "/linked-data/huc12pp/070900020601/navigation/DM/flowlines?distance=",
        HttpStatus.OK.value(),
        BaseController.MIME_TYPE_GEOJSON,
        getCompareFile(RESULT_FOLDER, "getHuc12ppDMTestEmptyDistance.json"),
        true,
        false);
  }

  @Test
  public void getHuc12ppDMTestMissingParameter() throws Exception {
    assertEntity(
        restTemplate,
        "/linked-data/huc12pp/070900020601/navigation/DM/flowlines",
        HttpStatus.BAD_REQUEST.value(),
        MediaType.APPLICATION_JSON_VALUE,
        "{\"description\":\"Required String parameter 'distance' is not present\",\"type\":\"error\"}",
        true,
        false);
  }

  @Test
  public void getNwisUpstreamTrimTest() throws Exception {
    assertEntity(
        restTemplate,
        "/linked-data/nwissite/USGS-05427850/navigation/UT/flowlines?distance=2&trimStart=true&trimTolerance=2",
        HttpStatus.OK.value(),
        BaseController.MIME_TYPE_GEOJSON,
        getCompareFile(RESULT_FOLDER, "getNwisUpstreamTrimTest_1.json"),
        true,
        false);

    assertEntity(
        restTemplate,
        "/linked-data/nwissite/USGS-05427850/navigation/UT/flowlines?distance=2&trimStart=true&trimTolerance=3",
        HttpStatus.OK.value(),
        BaseController.MIME_TYPE_GEOJSON,
        getCompareFile(RESULT_FOLDER, "getNwisUpstreamTrimTest_2.json"),
        true,
        false);

    assertEntity(
        restTemplate,
        "/linked-data/nwissite/USGS-05427850/navigation/UM/flowlines?distance=2&trimStart=true&trimTolerance=2",
        HttpStatus.OK.value(),
        BaseController.MIME_TYPE_GEOJSON,
        getCompareFile(RESULT_FOLDER, "getNwisUpstreamTrimTest_3.json"),
        true,
        false);

    assertEntity(
        restTemplate,
        "/linked-data/nwissite/USGS-05427850/navigation/UM/flowlines?distance=2&trimStart=true&trimTolerance=3",
        HttpStatus.OK.value(),
        BaseController.MIME_TYPE_GEOJSON,
        getCompareFile(RESULT_FOLDER, "getNwisUpstreamTrimTest_4.json"),
        true,
        false);
  }

  @Test
  public void getNwisDownstreamTrimTest() throws Exception {
    assertEntity(
        restTemplate,
        "/linked-data/nwissite/USGS-05427850/navigation/DD/flowlines?distance=2&trimStart=true&trimTolerance=2",
        HttpStatus.OK.value(),
        BaseController.MIME_TYPE_GEOJSON,
        getCompareFile(RESULT_FOLDER, "getNwisDownstreamTrimTest_1.json"),
        true,
        false);

    assertEntity(
        restTemplate,
        "/linked-data/nwissite/USGS-05427850/navigation/DD/flowlines?distance=2&trimStart=true&trimTolerance=3",
        HttpStatus.OK.value(),
        BaseController.MIME_TYPE_GEOJSON,
        getCompareFile(RESULT_FOLDER, "getNwisDownstreamTrimTest_2.json"),
        true,
        false);

    assertEntity(
        restTemplate,
        "/linked-data/nwissite/USGS-05427850/navigation/DM/flowlines?distance=2&trimStart=true&trimTolerance=2",
        HttpStatus.OK.value(),
        BaseController.MIME_TYPE_GEOJSON,
        getCompareFile(RESULT_FOLDER, "getNwisDownstreamTrimTest_3.json"),
        true,
        false);

    assertEntity(
        restTemplate,
        "/linked-data/nwissite/USGS-05427850/navigation/DM/flowlines?distance=2&trimStart=true&trimTolerance=3",
        HttpStatus.OK.value(),
        BaseController.MIME_TYPE_GEOJSON,
        getCompareFile(RESULT_FOLDER, "getNwisDownstreamTrimTest_3.json"),
        true,
        false);
  }

  @Test
  public void getNwisNoTrimTest() throws Exception {
    assertEntity(
        restTemplate,
        "/linked-data/nwissite/USGS-05427850/navigation/DM/flowlines?distance=2",
        HttpStatus.OK.value(),
        BaseController.MIME_TYPE_GEOJSON,
        getCompareFile(RESULT_FOLDER, "getNwisNoTrimTest_1.json"),
        true,
        false);

    assertEntity(
        restTemplate,
        "/linked-data/nwissite/USGS-05427850/navigation/UM/flowlines?distance=2",
        HttpStatus.OK.value(),
        BaseController.MIME_TYPE_GEOJSON,
        getCompareFile(RESULT_FOLDER, "getNwisNoTrimTest_2.json"),
        true,
        false);
  }

  @Test
  public void getWqpUpstreamTrimTest() throws Exception {
    assertEntity(
        restTemplate,
        "/linked-data/wqp/USGS-05427850/navigation/UT/flowlines?distance=2&trimStart=true&trimTolerance=2",
        HttpStatus.OK.value(),
        BaseController.MIME_TYPE_GEOJSON,
        getCompareFile(RESULT_FOLDER, "getWqpUpstreamTrimTest_1.json"),
        true,
        false);

    assertEntity(
        restTemplate,
        "/linked-data/wqp/USGS-05427850/navigation/UT/flowlines?distance=2&trimStart=true&trimTolerance=3",
        HttpStatus.OK.value(),
        BaseController.MIME_TYPE_GEOJSON,
        getCompareFile(RESULT_FOLDER, "getWqpUpstreamTrimTest_2.json"),
        true,
        false);

    assertEntity(
        restTemplate,
        "/linked-data/wqp/USGS-05427850/navigation/UM/flowlines?distance=2&trimStart=true&trimTolerance=2",
        HttpStatus.OK.value(),
        BaseController.MIME_TYPE_GEOJSON,
        getCompareFile(RESULT_FOLDER, "getWqpUpstreamTrimTest_3.json"),
        true,
        false);

    assertEntity(
        restTemplate,
        "/linked-data/wqp/USGS-05427850/navigation/UM/flowlines?distance=2&trimStart=true&trimTolerance=3",
        HttpStatus.OK.value(),
        BaseController.MIME_TYPE_GEOJSON,
        getCompareFile(RESULT_FOLDER, "getWqpUpstreamTrimTest_4.json"),
        true,
        false);
  }

  @Test
  public void getWqpDownstreamTrimTest() throws Exception {
    assertEntity(
        restTemplate,
        "/linked-data/wqp/USGS-05427850/navigation/DD/flowlines?distance=2&trimStart=true&trimTolerance=2",
        HttpStatus.OK.value(),
        BaseController.MIME_TYPE_GEOJSON,
        getCompareFile(RESULT_FOLDER, "getWqpDownstreamTrimTest_1.json"),
        true,
        false);

    assertEntity(
        restTemplate,
        "/linked-data/wqp/USGS-05427850/navigation/DD/flowlines?distance=2&trimStart=true&trimTolerance=3",
        HttpStatus.OK.value(),
        BaseController.MIME_TYPE_GEOJSON,
        getCompareFile(RESULT_FOLDER, "getWqpDownstreamTrimTest_2.json"),
        true,
        false);

    assertEntity(
        restTemplate,
        "/linked-data/wqp/USGS-05427850/navigation/DM/flowlines?distance=2&trimStart=true&trimTolerance=2",
        HttpStatus.OK.value(),
        BaseController.MIME_TYPE_GEOJSON,
        getCompareFile(RESULT_FOLDER, "getWqpDownstreamTrimTest_3.json"),
        true,
        false);

    assertEntity(
        restTemplate,
        "/linked-data/wqp/USGS-05427850/navigation/DM/flowlines?distance=2&trimStart=true&trimTolerance=3",
        HttpStatus.OK.value(),
        BaseController.MIME_TYPE_GEOJSON,
        getCompareFile(RESULT_FOLDER, "getWqpDownstreamTrimTest_4.json"),
        true,
        false);
  }

  @Test
  public void getWqpNoTrimTest() throws Exception {
    assertEntity(
        restTemplate,
        "/linked-data/wqp/USGS-05427850/navigation/DM/flowlines?distance=2",
        HttpStatus.OK.value(),
        BaseController.MIME_TYPE_GEOJSON,
        getCompareFile(RESULT_FOLDER, "getWqpNoTrimTest_1.json"),
        true,
        false);

    assertEntity(
        restTemplate,
        "/linked-data/wqp/USGS-05427850/navigation/UM/flowlines?distance=2",
        HttpStatus.OK.value(),
        BaseController.MIME_TYPE_GEOJSON,
        getCompareFile(RESULT_FOLDER, "getWqpNoTrimTest_2.json"),
        true,
        false);
  }

  @Test
  public void badInputTest() throws Exception {
    assertEntity(
        restTemplate,
        "/linked-data/wqx/USGS-05427880/navigation/DM/flowlines?distance=9999",
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
        "/linked-data/wqp/USGS-05427880/navigation/XX",
        HttpStatus.BAD_REQUEST.value(),
        MediaType.APPLICATION_JSON_VALUE,
        "{\"description\":\"getNavigation.navigationMode: must match 'DD|DM|PP|UT|UM'\",\"type\":\"error\"}",
        true,
        false);
  }
}
