package gov.usgs.owi.nldi.controllers;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import gov.usgs.owi.nldi.BaseIT;
import gov.usgs.owi.nldi.transform.BasinTransformer;

@EnableWebMvc
@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)
@DatabaseSetup("classpath:/testData/crawlerSource.xml")
public class CharacteristicsControllerIT extends BaseIT {

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	private static final String RESULT_FOLDER  = "characteristic/";

	@Before
	public void setup() {
		urlRoot = "http://localhost:" + port + context;
	}

	@Test
	public void getCharacteristicsTest() throws Exception {
		assertEntity(restTemplate,
				"/api/tot/characteristics",
				HttpStatus.OK.value(),
				null,
				null,
				BaseController.MIME_TYPE_GEOJSON,
				getCompareFile(RESULT_FOLDER, "meta/tot.json"),
				true,
				false);
	}

	@Test
	public void getCharacteristicDataTest() throws Exception {
		assertEntity(restTemplate,
				"/api/comid/13302592/tot",
				HttpStatus.OK.value(),
				null,
				null,
				BaseController.MIME_TYPE_GEOJSON,
				getCompareFile(RESULT_FOLDER, "data/comid_13302592_tot.json"),
				true,
				false);
	}

	@Test
	public void getCharacteristicDataFilteredTest() throws Exception {
		assertEntity(restTemplate,
				"/api/comid/13302592/tot?characteristicId=TOT_N97&characteristicId=TOT_ET",
				HttpStatus.OK.value(),
				null,
				null,
				BaseController.MIME_TYPE_GEOJSON,
				getCompareFile(RESULT_FOLDER, "data/comid_13302592_tot_filtered.json"),
				true,
				false);
	}

	@Test
	public void getBasinTest() throws Exception {
		assertEntity(restTemplate,
				"/api/comid/13302592/basin",
				HttpStatus.OK.value(),
				BasinTransformer.BASIN_COUNT_HEADER,
				"1",
				BaseController.MIME_TYPE_GEOJSON,
				getCompareFile(RESULT_FOLDER, "basin/comid_13302592.json"),
				true,
				false);
	}

}
