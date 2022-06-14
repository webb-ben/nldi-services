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
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DatabaseSetup("classpath:/testData/networkController/LegacyFlowline.xml")
public class NetworkControllerLegacyFlowlineIT extends BaseControllerIT {
  private final String RESULT_FOLDER = "networkController/legacyFlowline/";

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
        "/linked-data/comid/13293474/navigation/UT/flowlines?distance=9999&legacy=true",
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
        "/linked-data/comid/13297246/navigation/UT/flowlines?distance=1&legacy=true",
        HttpStatus.OK.value(),
        BaseController.MIME_TYPE_GEOJSON,
        getCompareFile(RESULT_FOLDER, "getComidUtDistanceTest.json"),
        true,
        false);
  }

  @Test
  public void getComidUtDiversionTest() throws Exception {
    assertEntity(
        restTemplate,
        "/linked-data/comid/13294158/navigation/UT/flowlines?distance=9999&legacy=true",
        HttpStatus.OK.value(),
        BaseController.MIME_TYPE_GEOJSON,
        getCompareFile(RESULT_FOLDER, "getComidUtDiversionTest.json"),
        true,
        false);
  }

  // UM Testing
  @Test
  public void getComidUmTest() throws Exception {
    assertEntity(
        restTemplate,
        "/linked-data/comid/13293474/navigation/UM/flowlines?distance=9999&legacy=true",
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
        "/linked-data/comid/13297246/navigation/UM/flowlines?distance=1&legacy=true",
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
        "/linked-data/comid/938060153/navigation/DM/flowlines?distance=9999&legacy=true",
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
        "/linked-data/comid/938060153/navigation/DM/flowlines?distance=20&legacy=true",
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
        "/linked-data/comid/938060153/navigation/DD/flowlines?distance=9999&legacy=true",
        HttpStatus.OK.value(),
        BaseController.MIME_TYPE_GEOJSON,
        getCompareFile(RESULT_FOLDER, "getComidDdTest.json"),
        true,
        false);
  }

  @Test
  public void getComidDdDistanceTest() throws Exception {
    // We are going to sacrifice a little accuracy for performance, so this does not match the old
    // way...
    assertEntity(
        restTemplate,
        "/linked-data/comid/938060153/navigation/DD/flowlines?distance=25&legacy=true",
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
        "/linked-data/comid/13297246/navigation/PP/flowlines?distance=9999&stopComid=13297198&legacy=true",
        HttpStatus.BAD_REQUEST.value(),
        null,
        "{\"description\":\"400 BAD_REQUEST 'The stopComid must be downstream of the start"
            + " comid.'\",\"type\":\"error\"}",
        true,
        true);
  }

  @Test
  public void getComidPpStopComidTest() throws Exception {
    assertEntity(
        restTemplate,
        "/linked-data/comid/13297198/navigation/PP/flowlines?distance=9999&stopComid=13297246&legacy=true",
        HttpStatus.OK.value(),
        BaseController.MIME_TYPE_GEOJSON,
        getCompareFile(RESULT_FOLDER, "getComidPpStopComidTest.json"),
        true,
        false);
  }

  // Interesting diversion/tributary
  // There is another simple diversion between 13294248 and 13294242
  @Test
  public void interestingTest() throws Exception {
    assertEntity(
        restTemplate,
        "/linked-data/comid/15169615/navigation/DM/flowlines?distance=50&legacy=true",
        HttpStatus.OK.value(),
        BaseController.MIME_TYPE_GEOJSON,
        getCompareFile(RESULT_FOLDER, "interestingTest_1.json"),
        true,
        false);

    assertEntity(
        restTemplate,
        "/linked-data/comid/15169615/navigation/DD/flowlines?distance=50&legacy=true",
        HttpStatus.OK.value(),
        BaseController.MIME_TYPE_GEOJSON,
        getCompareFile(RESULT_FOLDER, "interestingTest_2.json"),
        true,
        false);

    assertEntity(
        restTemplate,
        "/linked-data/comid/18719534/navigation/DM/flowlines?distance=50&legacy=true",
        HttpStatus.OK.value(),
        BaseController.MIME_TYPE_GEOJSON,
        getCompareFile(RESULT_FOLDER, "interestingTest_3.json"),
        true,
        false);

    assertEntity(
        restTemplate,
        "/linked-data/comid/18719534/navigation/DD/flowlines?distance=50&legacy=true",
        HttpStatus.OK.value(),
        BaseController.MIME_TYPE_GEOJSON,
        getCompareFile(RESULT_FOLDER, "interestingTest_4.json"),
        true,
        false);

    assertEntity(
        restTemplate,
        "/linked-data/comid/15183789/navigation/UM/flowlines?distance=50&legacy=true",
        HttpStatus.OK.value(),
        BaseController.MIME_TYPE_GEOJSON,
        getCompareFile(RESULT_FOLDER, "interestingTest_5.json"),
        true,
        false);

    assertEntity(
        restTemplate,
        "/linked-data/comid/15183789/navigation/UT/flowlines?distance=50&legacy=true",
        HttpStatus.OK.value(),
        BaseController.MIME_TYPE_GEOJSON,
        getCompareFile(RESULT_FOLDER, "interestingTest_6.json"),
        true,
        false);
  }
}
