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
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import gov.usgs.owi.nldi.BaseIT;
import gov.usgs.owi.nldi.transform.FeatureTransformer;

@EnableWebMvc
@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)
@DatabaseSetup("classpath:/testData/deprecated/linkedDataController/DataSource.xml")
// This test class contains tests for the deprecated "navigate" endpoints.  Don't add
// new tests here and delete this class when we drop support for those endpoints.
// The new tests that are tied to the new "navigation" endpoints are in
// LinkedDataControllerDataSourceIT
public class DeprecatedLinkedDataControllerDataSourceIT extends BaseControllerIT {
	private final String RESULT_FOLDER  = "deprecated/linkedDataController/dataSource/";

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	@BeforeEach
	public void setUp() {
		urlRoot = "http://localhost:" + port + context;
	}

	//Navigation Within Datasource Testing
	@Test
	public void getWqpUtTest() throws Exception {
		assertEntity(restTemplate,
				"/linked-data/wqp/USGS-05427880/navigate/UT/wqp",
				HttpStatus.OK.value(),
				FeatureTransformer.FEATURE_COUNT_HEADER,
				"4",
				BaseController.MIME_TYPE_GEOJSON,
				getCompareFile(RESULT_FOLDER, "getWqpUtTest.json"),
				true,
				false);
	}

	@Test
	public void getWqpUtTestDistance1() throws Exception {
		assertEntity(restTemplate,
				"/linked-data/wqp/USGS-05427880/navigate/UT/wqp?distance=1",
				HttpStatus.OK.value(),
				FeatureTransformer.FEATURE_COUNT_HEADER,
				"4",
				BaseController.MIME_TYPE_GEOJSON,
				getCompareFile(RESULT_FOLDER, "getWqpUtTestDistance1.json"),
				true,
				false);

	}


	@Test
	public void getWqpUtTestDistanceEmpty() throws Exception {
		assertEntity(restTemplate,
				"/linked-data/wqp/USGS-05427880/navigate/UT/wqp?distance=",
				HttpStatus.OK.value(),
				FeatureTransformer.FEATURE_COUNT_HEADER,
				"4",
				BaseController.MIME_TYPE_GEOJSON,
				getCompareFile(RESULT_FOLDER, "getWqpUtTestDistanceEmpty.json"),
				true,
				false);
	}

	@Test
	public void getWqpUtTestDistanceAboveMax() throws Exception {
		assertEntity(restTemplate,
				"/linked-data/wqp/USGS-05427880/navigate/UT/wqp?distance=10000",
				HttpStatus.BAD_REQUEST.value(),
				null,
				null,
				null,
				"getFeaturesDeprecated.distance: distance must be between 1 and 9999 kilometers",
				false,
				false);
	}


	@Test
	public void getWqpUtTestDistanceBelowMin() throws Exception {
		assertEntity(restTemplate,
				"/linked-data/wqp/USGS-05427880/navigate/UT/wqp?distance=-1",
				HttpStatus.BAD_REQUEST.value(),
				null,
				null,
				null,
				"getFeaturesDeprecated.distance: distance must be between 1 and 9999 kilometers",
				false,
				false);
	}

	//Navigation Different Datasource Testing
	@Test
	public void getWqpDmTest() throws Exception {
		assertEntity(restTemplate,
				"/linked-data/wqp/USGS-05427880/navigate/DM/huc12pp",
				HttpStatus.OK.value(),
				FeatureTransformer.FEATURE_COUNT_HEADER,
				"1",
				BaseController.MIME_TYPE_GEOJSON,
				getCompareFile(RESULT_FOLDER, "getWqpDmTest.json"),
				true,
				false);
	}

	@Test
	public void badInputTest() throws Exception {
		assertEntity(restTemplate,
				"/linked-data/wqx/USGS-05427880/navigate/DM/huc12pp",
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
				"/linked-data/wqp/USGS-05427880/navigate/XX/huc12pp",
				HttpStatus.BAD_REQUEST.value(),
				null,
				null,
				null,
				"getFeaturesDeprecated.navigationMode: must match \"DD|DM|PP|UT|UM\"",
				false,
				false);
	}

	//Navigation Types Testing
	@Test
	public void getNavigationTypesTest() throws Exception {
		assertEntity(restTemplate,
			"/linked-data/wqp/USGS-05427880/navigate",
			HttpStatus.OK.value(),
			null,
			null,
			MediaType.APPLICATION_JSON_VALUE,
			getCompareFile(RESULT_FOLDER, "getNavigationTypesTest.json"),
			true,
			false);
	}

	@Test
	public void getNavigationTypesNotFoundTest() throws Exception {
		assertEntity(restTemplate,
			"/linked-data/wqx/USGS-05427880/navigate",
			HttpStatus.NOT_FOUND.value(),
			null,
			null,
			MediaType.APPLICATION_JSON_VALUE,
			null,
			true,
			false);

		assertEntity(restTemplate,
			"/linked-data/wqp/USGX-05427880/navigate",
			HttpStatus.NOT_FOUND.value(),
			null,
			null,
			MediaType.APPLICATION_JSON_VALUE,
			null,
			true,
			false);
	}
}
