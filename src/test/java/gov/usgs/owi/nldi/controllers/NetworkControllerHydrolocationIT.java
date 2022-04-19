package gov.usgs.owi.nldi.controllers;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import gov.usgs.owi.nldi.BaseIT;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DatabaseSetup("classpath:/testData/nldi_data/crawler_source.xml")
@DatabaseSetup("classpath:/testData/nldi_data/feature/wqp.xml")
@DatabaseSetup("classpath:/testData/nhdplus/nhdflowline_np21.xml")
public class NetworkControllerHydrolocationIT extends BaseIT {

  @Value("${serverContextPath}")
  private String context;

  @LocalServerPort private int port;

  @Autowired private TestRestTemplate restTemplate;
  private static final String RESULT_FOLDER = "network/hydrolocation/";

  @BeforeEach
  public void setUp() {
    urlRoot = "http://localhost:" + port + context;
  }

  @Test
  public void getHydrolocationTest() throws Exception {
    assertEntity(
        restTemplate,
        "/linked-data/hydrolocation?coords=POINT(-89.55 43.2)",
        HttpStatus.OK.value(),
        null,
        null,
        BaseController.MIME_TYPE_GEOJSON,
        getCompareFile(RESULT_FOLDER, "hydro_1.json"),
        true,
        true);
  }

  @Test
  public void getHydrolocationTestMalformedNumber() throws Exception {
    assertEntity(
        restTemplate,
        "/linked-data/hydrolocation?coords=POINT(-89.35 NotANumber)",
        HttpStatus.BAD_REQUEST.value(),
        null,
        null,
        null,
        null,
        false,
        false);
  }

  @Test
  public void getHydrolocationTestOutOfRange() throws Exception {
    assertEntity(
        restTemplate,
        "/linked-data/hydrolocation?coords=POINT(-181 0)",
        HttpStatus.BAD_REQUEST.value(),
        null,
        null,
        null,
        "400 BAD_REQUEST \"Invalid longitude\"",
        false,
        false);

    assertEntity(
        restTemplate,
        "/linked-data/hydrolocation?coords=POINT(181 0)",
        HttpStatus.BAD_REQUEST.value(),
        null,
        null,
        null,
        "400 BAD_REQUEST \"Invalid longitude\"",
        false,
        false);

    assertEntity(
        restTemplate,
        "/linked-data/hydrolocation?coords=POINT(0 -91)",
        HttpStatus.BAD_REQUEST.value(),
        null,
        null,
        null,
        "400 BAD_REQUEST \"Invalid latitude\"",
        false,
        false);

    assertEntity(
        restTemplate,
        "/linked-data/hydrolocation?coords=POINT(0 91)",
        HttpStatus.BAD_REQUEST.value(),
        null,
        null,
        null,
        "400 BAD_REQUEST \"Invalid latitude\"",
        false,
        false);
  }

  @Test
  public void getHydrolocationTestMalformedParam() throws Exception {
    assertEntity(
        restTemplate,
        "/linked-data/hydrolocation?coords=POINTBAD(-89.35 43.0864)",
        HttpStatus.BAD_REQUEST.value(),
        null,
        null,
        null,
        null,
        false,
        false);
  }

  @Test
  public void getHydrolocationTestNoCoordinates() throws Exception {
    assertEntity(
        restTemplate,
        "/linked-data/hydrolocation",
        HttpStatus.BAD_REQUEST.value(),
        null,
        null,
        null,
        null,
        false,
        false);
  }
}