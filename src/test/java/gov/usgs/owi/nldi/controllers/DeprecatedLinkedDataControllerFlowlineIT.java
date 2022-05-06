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
@DatabaseSetup("classpath:/testData/deprecated/linkedDataController/Flowline.xml")
// This test class contains tests for the deprecated "navigate" endpoints.  Don't add
// new tests here and delete this class when we drop support for those endpoints.
// The new tests that are tied to the new "navigation" endpoints are in
// LinkedDataControllerFlowlineIT
public class DeprecatedLinkedDataControllerFlowlineIT extends BaseControllerIT {
	private final String RESULT_FOLDER  = "deprecated/linkedDataController/flowline/";

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	@BeforeEach
	public void setUp() {
		urlRoot = "http://localhost:" + port + context;
	}

	@Test
	public void getWqpUMTest() throws Exception {
		assertEntity(restTemplate,
				"/linked-data/wqp/USGS-05427880/navigate/UM",
				HttpStatus.OK.value(),
				FlowLineTransformer.FLOW_LINES_COUNT_HEADER,
				"10",
				BaseController.MIME_TYPE_GEOJSON,
				getCompareFile(RESULT_FOLDER, "getWqpUMTest.json"),
				true,
				false);
	}

	@Test
	public void getHuc12ppDM10Test() throws Exception {
		assertEntity(restTemplate,
				"/linked-data/huc12pp/070900020601/navigate/DM?distance=10",
				HttpStatus.OK.value(),
				FlowLineTransformer.FLOW_LINES_COUNT_HEADER,
				"6",
				BaseController.MIME_TYPE_GEOJSON,
				getCompareFile(RESULT_FOLDER, "getHuc12ppDM10Test.json"),
				true,
				false);
	}


	@Test
	public void getHuc12ppDM10000TestDistanceAboveMax() throws Exception {
		assertEntity(restTemplate,
				"/linked-data/huc12pp/070900020601/navigate/DM?distance=10000",
				HttpStatus.BAD_REQUEST.value(),
				null,
				null,
				null,
				"getFlowlines.distance: distance must be between 1 and 9999 kilometers",
				false,
				false);
	}

	@Test
	public void getHuc12ppDM0TestDistanceBelowMin() throws Exception {
		assertEntity(restTemplate,
				"/linked-data/huc12pp/070900020601/navigate/DM?distance=-1",
				HttpStatus.BAD_REQUEST.value(),
				null,
				null,
				null,
				"getFlowlines.distance: distance must be between 1 and 9999 kilometers",
				false,
				false);
	}


	@Test
	public void getHuc12ppDMTestEmptyDistance() throws Exception {
		assertEntity(restTemplate,
				"/linked-data/huc12pp/070900020601/navigate/DM?distance=",
				HttpStatus.OK.value(),
				FlowLineTransformer.FLOW_LINES_COUNT_HEADER,
				"51",
				BaseController.MIME_TYPE_GEOJSON,
				getCompareFile(RESULT_FOLDER, "getHuc12ppDMTestEmptyDistance.json"),
				true,
				false);
	}

	@Test
	public void badInputTest() throws Exception {
		assertEntity(restTemplate,
				"/linked-data/wqx/USGS-05427880/navigate/DM",
				HttpStatus.NOT_FOUND.value(),
				null,
				null,
				null,
				null,
				true,
				false);
	}

	//Parameter Error Testing
	@Test
	public void badNavigationModeTest() throws Exception {
		assertEntity(restTemplate,
				"/linked-data/wqp/USGS-05427880/navigate/XX",
				HttpStatus.BAD_REQUEST.value(),
				null,
				null,
				null,
				"getFlowlines.navigationMode: must match \"DD|DM|PP|UT|UM\"",
				false,
				false);
	}
}
