package gov.usgs.owi.nldi.controllers;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
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
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

@EnableWebMvc
@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)
@DatabaseSetup("classpath:/testData/LookupController.xml")
public class LookupControllerIT extends BaseControllerIT {
	private final String RESULT_FOLDER  = "lookupController/";

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	@BeforeEach
	public void setUp() {
		urlRoot = "http://localhost:" + port + context;
	}

	@Test
	public void getCharacteristicsTest() throws Exception {
		assertEntity(restTemplate,
				"/lookups/tot/characteristics",
				HttpStatus.OK.value(),
				null,
				null,
				BaseController.MIME_TYPE_GEOJSON,
				getCompareFile(RESULT_FOLDER, "getCharacteristicsTest.json"),
				true,
				false);
	}

	@Test
	public void getLookupsTest() throws Exception {
		assertEntity(restTemplate,
				"/lookups?f=json",
				HttpStatus.OK.value(),
				null,
				null,
				null,
				getCompareFile(RESULT_FOLDER, "getLookupsTest.json"),
				true,
				false);
	}

	@Test
	public void getLookupsRedirectTest() {
		ResponseEntity<String> rtn = restTemplate.getForEntity("/lookups/tot?f=json", String.class);
		assertThat(rtn.getStatusCode(), equalTo(HttpStatus.FOUND)); // 302 FOUND redirect
	}
}
