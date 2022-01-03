package gov.usgs.owi.nldi.controllers;

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

import com.github.springtestdbunit.annotation.DatabaseSetup;

import gov.usgs.owi.nldi.BaseIT;
import gov.usgs.owi.nldi.transform.FlowLineTransformer;


@EnableWebMvc
@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)
@DatabaseSetup("classpath:/testData/nldi_data/crawler_source.xml")
// This test class contains tests for the deprecated "navigate" endpoints.  Don't add
// new tests here and delete this class when we drop support for those endpoints.
// The new tests that are tied to the new "navigation" endpoints are in
// NetworkControllerLegacyFlowlineIT
public class DeprecatedNetworkControllerLegacyFlowlineIT extends BaseIT {

	@Value("${serverContextPath}")
	private String context;

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	private static final String RESULT_FOLDER  = "network/flowline/";

	@BeforeEach
	public void setUp() {
		urlRoot = "http://localhost:" + port + context;
	}

	//UT Testing
	@Test
	public void getComidUtTest() throws Exception {
		assertEntity(restTemplate,
				"/linked-data/comid/13293474/navigate/UT?legacy=true",
				HttpStatus.OK.value(),
				FlowLineTransformer.FLOW_LINES_COUNT_HEADER,
				"7",
				BaseController.MIME_TYPE_GEOJSON,
				getCompareFile(RESULT_FOLDER, "comid_13293474_UT.json"),
				true,
				false);
	}

	@Test
	public void getComidUtDistanceTest() throws Exception {
		assertEntity(restTemplate,
				"/linked-data/comid/13297246/navigate/UT?distance=1&legacy=true",
				HttpStatus.OK.value(),
				FlowLineTransformer.FLOW_LINES_COUNT_HEADER,
				"2",
				BaseController.MIME_TYPE_GEOJSON,
				getCompareFile(RESULT_FOLDER, "comid_13297246_UT_distance_1.json"),
				true,
				false);
	}

	@Test
	public void getComidUtDiversionTest() throws Exception {
		assertEntity(restTemplate,
				"/linked-data/comid/13294158/navigate/UT?legacy=true",
				HttpStatus.OK.value(),
				FlowLineTransformer.FLOW_LINES_COUNT_HEADER,
				"15",
				BaseController.MIME_TYPE_GEOJSON,
				getCompareFile(RESULT_FOLDER, "comid_13294158_UT.json"),
				true,
				false);
	}

	//UM Testing
	@Test
	public void getComidUmTest() throws Exception {
		assertEntity(restTemplate,
				"/linked-data/comid/13293474/navigate/UM?legacy=true",
				HttpStatus.OK.value(),
				FlowLineTransformer.FLOW_LINES_COUNT_HEADER,
				"4",
				BaseController.MIME_TYPE_GEOJSON,
				getCompareFile(RESULT_FOLDER, "comid_13293474_UM.json"),
				true,
				false);
	}

	@Test
	public void getComidUmDistanceTest() throws Exception {
		assertEntity(restTemplate,
				"/linked-data/comid/13297246/navigate/UM?distance=1&legacy=true",
				HttpStatus.OK.value(),
				FlowLineTransformer.FLOW_LINES_COUNT_HEADER,
				"2",
				BaseController.MIME_TYPE_GEOJSON,
				getCompareFile(RESULT_FOLDER, "comid_13297246_UM_distance_1.json"),
				true,
				false);
	}

	//DM Testing
	@Test
	public void getComidDmTest() throws Exception {
		assertEntity(restTemplate,
				"/linked-data/comid/938060153/navigate/DM?legacy=true",
				HttpStatus.OK.value(),
				FlowLineTransformer.FLOW_LINES_COUNT_HEADER,
				"112",
				BaseController.MIME_TYPE_GEOJSON,
				getCompareFile(RESULT_FOLDER, "comid_938060153_DM.json"),
				true,
				false);
	}

	@Test
	public void getComidDmDiversionsNotIncludedTest() throws Exception {
		assertEntity(restTemplate,
				"/linked-data/comid/938060153/navigate/DM?legacy=true",
				HttpStatus.OK.value(),
				FlowLineTransformer.FLOW_LINES_COUNT_HEADER,
				"112",
				BaseController.MIME_TYPE_GEOJSON,
				getCompareFile(RESULT_FOLDER, "comid_938060153_DM.json"),
				true,
				false);
	}

	@Test
	public void getComidDmDistanceTest() throws Exception {
		assertEntity(restTemplate,
				"/linked-data/comid/938060153/navigate/DM?distance=20&legacy=true",
				HttpStatus.OK.value(),
				FlowLineTransformer.FLOW_LINES_COUNT_HEADER,
				"4",
				BaseController.MIME_TYPE_GEOJSON,
				getCompareFile(RESULT_FOLDER, "comid_938060153_DM_distance_20.json"),
				true,
				false);
	}

	//DD Testing
	@Test
	public void getComidDdTest() throws Exception {
		assertEntity(restTemplate,
				"/linked-data/comid/938060153/navigate/DD?legacy=true",
				HttpStatus.OK.value(),
				FlowLineTransformer.FLOW_LINES_COUNT_HEADER,
				"768",
				BaseController.MIME_TYPE_GEOJSON,
				getCompareFile(RESULT_FOLDER, "comid_938060153_DD_legacy.json"),
				true,
				false);
	}

	@Test
	public void getComidDdDistanceTest() throws Exception {
		//We are going to sacrifice a little accuracy for performance, so this does not match the old way...
		assertEntity(restTemplate,
				"/linked-data/comid/938060153/navigate/DD?distance=25&legacy=true",
				HttpStatus.OK.value(),
				FlowLineTransformer.FLOW_LINES_COUNT_HEADER,
				"4",
				BaseController.MIME_TYPE_GEOJSON,
				getCompareFile(RESULT_FOLDER, "comid_938060153_DD_distance_25_legacy.json"),
				true,
				false);
	}

	//PP Testing
	@Test
	public void getComidPpStopComidInvalidTest() throws Exception {
		assertEntity(restTemplate,
				"/linked-data/comid/13297246/navigate/PP?stopComid=13297198&legacy=true",
				HttpStatus.BAD_REQUEST.value(),
				null,
				null,
				null,
				"400 BAD_REQUEST \"The stopComid must be downstream of the start comid.\"",
				false,
				true);
	}

	@Test
	public void getComidPpStopComidTest() throws Exception {
		assertEntity(restTemplate,
				"/linked-data/comid/13297198/navigate/PP?stopComid=13297246&legacy=true",
				HttpStatus.OK.value(),
				FlowLineTransformer.FLOW_LINES_COUNT_HEADER,
				"12",
				BaseController.MIME_TYPE_GEOJSON,
				getCompareFile(RESULT_FOLDER, "comid_13297198_PP_stop_13297246_legacy.json"),
				true,
				false);
	}

	//Interesting diversion/tributary
	//There is another simple diversion between 13294248 and 13294242
	@Test
	public void interestingTest() throws Exception {
		assertEntity(restTemplate,
				"/linked-data/comid/15169615/navigate/DM?distance=50&legacy=true",
				HttpStatus.OK.value(),
				FlowLineTransformer.FLOW_LINES_COUNT_HEADER,
				"9",
				BaseController.MIME_TYPE_GEOJSON,
				getCompareFile(RESULT_FOLDER, "comid_15169615_DM_distance_50.json"),
				true,
				false);

		assertEntity(restTemplate,
				"/linked-data/comid/15169615/navigate/DD?distance=50&legacy=true",
				HttpStatus.OK.value(),
				FlowLineTransformer.FLOW_LINES_COUNT_HEADER,
				"13",
				BaseController.MIME_TYPE_GEOJSON,
				getCompareFile(RESULT_FOLDER, "comid_15169615_DD_distance_50_legacy.json"),
				true,
				false);

		assertEntity(restTemplate,
				"/linked-data/comid/18719534/navigate/DM?distance=50&legacy=true",
				HttpStatus.OK.value(),
				FlowLineTransformer.FLOW_LINES_COUNT_HEADER,
				"28",
				BaseController.MIME_TYPE_GEOJSON,
				getCompareFile(RESULT_FOLDER, "comid_18719534_DM_distance_50.json"),
				true,
				false);

		assertEntity(restTemplate,
				"/linked-data/comid/18719534/navigate/DD?distance=50&legacy=true",
				HttpStatus.OK.value(),
				FlowLineTransformer.FLOW_LINES_COUNT_HEADER,
				"225",
				BaseController.MIME_TYPE_GEOJSON,
				getCompareFile(RESULT_FOLDER, "comid_18719534_DD_distance_50_legacy.json"),
				true,
				false);

		assertEntity(restTemplate,
				"/linked-data/comid/15183789/navigate/UM?distance=50&legacy=true",
				HttpStatus.OK.value(),
				FlowLineTransformer.FLOW_LINES_COUNT_HEADER,
				"31",
				BaseController.MIME_TYPE_GEOJSON,
				getCompareFile(RESULT_FOLDER, "comid_15183789_UM_distance_50.json"),
				true,
				false);

		assertEntity(restTemplate,
				"/linked-data/comid/15183789/navigate/UT?distance=50&legacy=true",
				HttpStatus.OK.value(),
				FlowLineTransformer.FLOW_LINES_COUNT_HEADER,
				"31",
				BaseController.MIME_TYPE_GEOJSON,
				getCompareFile(RESULT_FOLDER, "comid_15183789_UT_distance_50_legacy.json"),
				true,
				false);
	}
}
