package gov.usgs.owi.nldi.controllers;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
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
@DatabaseSetup("classpath:/testData/deprecated/networkController/Flowline.xml")
// This test class contains tests for the deprecated "navigate" endpoints.  Don't add
// new tests here and delete this class when we drop support for those endpoints.
// The new tests that are tied to the new "navigation" endpoints are in
// NetworkControllerFlowlineIT
public class DeprecatedNetworkControllerFlowlineIT extends BaseControllerIT {
	private final String RESULT_FOLDER  = "deprecated/networkController/flowline/";

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	@BeforeEach
	public void setUp() {
		urlRoot = "http://localhost:" + port + context;
	}

	//UT Testing
	@Test
	public void getComidUtTest() throws Exception {
		assertEntity(restTemplate,
				"/linked-data/comid/13293474/navigate/UT",
				HttpStatus.OK.value(),
				FlowLineTransformer.FLOW_LINES_COUNT_HEADER,
				"7",
				BaseController.MIME_TYPE_GEOJSON,
				getCompareFile(RESULT_FOLDER, "getComidUtTest.json"),
				true,
				false);
	}

	@Test
	public void getComidUtDistanceTest() throws Exception {
		assertEntity(restTemplate,
				"/linked-data/comid/13297246/navigate/UT?distance=1",
				HttpStatus.OK.value(),
				FlowLineTransformer.FLOW_LINES_COUNT_HEADER,
				"2",
				BaseController.MIME_TYPE_GEOJSON,
				getCompareFile(RESULT_FOLDER, "getComidUtDistanceTest.json"),
				true,
				false);
	}


	@Test
	public void getComidUtDistanceTestEmpty() throws Exception {
		assertEntity(restTemplate,
				"/linked-data/comid/13297246/navigate/UT?distance=",
				HttpStatus.OK.value(),
				FlowLineTransformer.FLOW_LINES_COUNT_HEADER,
				"61",
				BaseController.MIME_TYPE_GEOJSON,
				getCompareFile(RESULT_FOLDER, "getComidUtDistanceTestEmpty.json"),
				true,
				false);
	}

	@Test
	public void getComidUtDistanceTestAboveMax() throws Exception {
		assertEntity(restTemplate,
				"/linked-data/comid/13297246/navigate/UT?distance=10000",
				HttpStatus.BAD_REQUEST.value(),
				null,
				null,
				null,
				"getFlowlines.distance: distance must be between 1 and 9999 kilometers",
				false,
				false);
	}

	@Test
	public void getComidUtDistanceTestBelowMin() throws Exception {
		assertEntity(restTemplate,
				"/linked-data/comid/13297246/navigate/UT?distance=-1",
				HttpStatus.BAD_REQUEST.value(),
				null,
				null,
				null,
				"getFlowlines.distance: distance must be between 1 and 9999 kilometers",
				false,
				false);
	}

	@Test
	public void getComidUtDiversionTest() throws Exception {
		assertEntity(restTemplate,
				"/linked-data/comid/13294158/navigate/UT",
				HttpStatus.OK.value(),
				FlowLineTransformer.FLOW_LINES_COUNT_HEADER,
				"15",
				BaseController.MIME_TYPE_GEOJSON,
				getCompareFile(RESULT_FOLDER, "getComidUtDiversionTest.json"),
				true,
				false);
	}

	//UM Testing
	@Test
	public void getComidUmTest() throws Exception {
		assertEntity(restTemplate,
				"/linked-data/comid/13293474/navigate/UM",
				HttpStatus.OK.value(),
				FlowLineTransformer.FLOW_LINES_COUNT_HEADER,
				"4",
				BaseController.MIME_TYPE_GEOJSON,
				getCompareFile(RESULT_FOLDER, "getComidUmTest.json"),
				true,
				false);
	}

	@Test
	public void getComidUmDistanceTest() throws Exception {
		assertEntity(restTemplate,
				"/linked-data/comid/13297246/navigate/UM?distance=1",
				HttpStatus.OK.value(),
				FlowLineTransformer.FLOW_LINES_COUNT_HEADER,
				"2",
				BaseController.MIME_TYPE_GEOJSON,
				getCompareFile(RESULT_FOLDER, "getComidUmDistanceTest.json"),
				true,
				false);
	}

	//DM Testing
	@Test
	public void getComidDmTest() throws Exception {
		assertEntity(restTemplate,
				"/linked-data/comid/938060153/navigate/DM",
				HttpStatus.OK.value(),
				FlowLineTransformer.FLOW_LINES_COUNT_HEADER,
				"112",
				BaseController.MIME_TYPE_GEOJSON,
				getCompareFile(RESULT_FOLDER, "getComidDmTest.json"),
				true,
				false);
	}

	//DD Testing
	@Test
	public void getComidDdTest() throws Exception {
		assertEntity(restTemplate,
				"/linked-data/comid/938060153/navigate/DD",
				HttpStatus.OK.value(),
				FlowLineTransformer.FLOW_LINES_COUNT_HEADER,
				"837",
				BaseController.MIME_TYPE_GEOJSON,
				getCompareFile(RESULT_FOLDER, "getComidDdTest.json"),
				true,
				false);
	}

	@Test
	public void getComidDdDistanceTest() throws Exception {
		assertEntity(restTemplate,
				"/linked-data/comid/938060153/navigate/DD?distance=25",
				HttpStatus.OK.value(),
				FlowLineTransformer.FLOW_LINES_COUNT_HEADER,
				"4",
				BaseController.MIME_TYPE_GEOJSON,
				getCompareFile(RESULT_FOLDER, "getComidDdDistanceTest.json"),
				true,
				false);
	}

	//PP Testing
	@Test
	public void getComidPpStopComidInvalidTest() throws Exception {
		// This deprecated endpoint and test are sharing the result file with
		// the current endpoint and test, so tweak the comparison string accordingly.
		assertEntity(restTemplate,
				"/linked-data/comid/13297246/navigate/PP?stopComid=13297198",
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
				"/linked-data/comid/13297198/navigate/PP?stopComid=13297246",
				HttpStatus.OK.value(),
				FlowLineTransformer.FLOW_LINES_COUNT_HEADER,
				"12",
				BaseController.MIME_TYPE_GEOJSON,
				getCompareFile(RESULT_FOLDER, "getComidPpStopComidTest.json"),
				true,
				false);
	}

	//Interesting diversion/tributary
	//There is another simple diversion between 13294248 and 13294242
	@Test
	public void interestingTest() throws Exception {
		assertEntity(restTemplate,
				"/linked-data/comid/15169615/navigate/DM?distance=50",
				HttpStatus.OK.value(),
				FlowLineTransformer.FLOW_LINES_COUNT_HEADER,
				"9",
				BaseController.MIME_TYPE_GEOJSON,
				getCompareFile(RESULT_FOLDER, "interestingTest_1.json"),
				true,
				false);

		assertEntity(restTemplate,
				"/linked-data/comid/15169615/navigate/DD?distance=50",
				HttpStatus.OK.value(),
				FlowLineTransformer.FLOW_LINES_COUNT_HEADER,
				"20",
				BaseController.MIME_TYPE_GEOJSON,
				getCompareFile(RESULT_FOLDER, "interestingTest_2.json"),
				true,
				false);

		assertEntity(restTemplate,
				"/linked-data/comid/18719534/navigate/DM?distance=50",
				HttpStatus.OK.value(),
				FlowLineTransformer.FLOW_LINES_COUNT_HEADER,
				"28",
				BaseController.MIME_TYPE_GEOJSON,
				getCompareFile(RESULT_FOLDER, "interestingTest_3.json"),
				true,
				false);

		assertEntity(restTemplate,
				"/linked-data/comid/18719534/navigate/DD?distance=50",
				HttpStatus.OK.value(),
				FlowLineTransformer.FLOW_LINES_COUNT_HEADER,
				"281",
				BaseController.MIME_TYPE_GEOJSON,
				getCompareFile(RESULT_FOLDER, "interestingTest_4.json"),
				true,
				false);

		assertEntity(restTemplate,
				"/linked-data/comid/15183789/navigate/UM?distance=50",
				HttpStatus.OK.value(),
				FlowLineTransformer.FLOW_LINES_COUNT_HEADER,
				"31",
				BaseController.MIME_TYPE_GEOJSON,
				getCompareFile(RESULT_FOLDER, "interestingTest_5.json"),
				true,
				false);

		assertEntity(restTemplate,
				"/linked-data/comid/15183789/navigate/UT?distance=50",
				HttpStatus.OK.value(),
				FlowLineTransformer.FLOW_LINES_COUNT_HEADER,
				"73",
				BaseController.MIME_TYPE_GEOJSON,
				getCompareFile(RESULT_FOLDER, "interestingTest_6.json"),
				true,
				false);
	}

	//Parameter Error Testing
	@Test
	public void badNavigationModeTest() throws Exception {
		assertEntity(restTemplate,
				"/linked-data/comid/13297198/navigate/XX",
				HttpStatus.BAD_REQUEST.value(),
				null,
				null,
				null,
				"getFlowlines.navigationMode: must match \"DD|DM|PP|UT|UM\"",
				false,
				false);
	}

	@Test
	public void getComidUmDistanceTestNavigation() throws Exception {
		assertEntity(restTemplate,
			"/linked-data/comid/13297246/navigate/UM?f=json&distance=1",
			HttpStatus.OK.value(),
			FlowLineTransformer.FLOW_LINES_COUNT_HEADER,
			"2",
			BaseController.MIME_TYPE_GEOJSON,
			getCompareFile(RESULT_FOLDER, "getComidUmDistanceTestNavigation.json"),
			true,
			false);
	}

	//DM Testing

	@Test
	public void getComidDmDistanceTestNavigation() throws Exception {
		assertEntity(restTemplate,
			"/linked-data/comid/938060153/navigate/DM?distance=20",
			HttpStatus.OK.value(),
			FlowLineTransformer.FLOW_LINES_COUNT_HEADER,
			"4",
			BaseController.MIME_TYPE_GEOJSON,
			getCompareFile(RESULT_FOLDER, "getComidDmDistanceTestNavigation.json"),
			true,
			false);
	}
}
