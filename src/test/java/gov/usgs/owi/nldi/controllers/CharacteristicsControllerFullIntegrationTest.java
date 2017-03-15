package gov.usgs.owi.nldi.controllers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.co.datumedge.hamcrest.json.SameJSONAs.sameJSONObjectAs;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import gov.usgs.owi.nldi.BaseSpringTest;
import gov.usgs.owi.nldi.FullIntegrationTest;
import gov.usgs.owi.nldi.transform.BasinTransformer;

@Category(FullIntegrationTest.class)
@DatabaseSetup("classpath:/testData/crawlerSource.xml")
public class CharacteristicsControllerFullIntegrationTest extends BaseSpringTest {

	@Autowired
	private WebApplicationContext wac;

	private MockMvc mockMvc;

	private static final String RESULT_FOLDER  = "characteristic/";

	@Before
	public void setup() {
		mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
	}

	@Test
	public void getCharacteristicsTest() throws Exception {
		MvcResult rtn = mockMvc.perform(get("/tot/characteristics"))
				.andExpect(status().isOk())
				.andExpect(header().string(NetworkController.HEADER_CONTENT_TYPE, NetworkController.MIME_TYPE_GEOJSON))
				.andReturn();

		assertThat(new JSONObject(rtn.getResponse().getContentAsString()),
				sameJSONObjectAs(new JSONObject(getCompareFile(RESULT_FOLDER, "meta/tot.json"))).allowingAnyArrayOrdering());
	}

	@Test
	public void getCharacteristicDataTest() throws Exception {
		MvcResult rtn = mockMvc.perform(get("/comid/13302592/tot"))
				.andExpect(status().isOk())
				.andExpect(header().string(NetworkController.HEADER_CONTENT_TYPE, NetworkController.MIME_TYPE_GEOJSON))
				.andReturn();

		assertThat(new JSONObject(rtn.getResponse().getContentAsString()),
				sameJSONObjectAs(new JSONObject(getCompareFile(RESULT_FOLDER, "data/comid_13302592_tot.json"))).allowingAnyArrayOrdering());
	}

	@Test
	public void getCharacteristicDataFilteredTest() throws Exception {
		MvcResult rtn = mockMvc.perform(get("/comid/13302592/tot?characteristicId=TOT_N97&characteristicId=TOT_ET"))
				.andExpect(status().isOk())
				.andExpect(header().string(NetworkController.HEADER_CONTENT_TYPE, NetworkController.MIME_TYPE_GEOJSON))
				.andReturn();

		assertThat(new JSONObject(rtn.getResponse().getContentAsString()),
				sameJSONObjectAs(new JSONObject(getCompareFile(RESULT_FOLDER, "data/comid_13302592_tot_filtered.json"))).allowingAnyArrayOrdering());
	}

	@Test
	public void getBasinTest() throws Exception {
		MvcResult rtn = mockMvc.perform(get("/comid/13302592/basin"))
				.andExpect(status().isOk())
				.andExpect(header().string(BasinTransformer.BASIN_COUNT_HEADER, "1"))
				.andExpect(header().string(NetworkController.HEADER_CONTENT_TYPE, NetworkController.MIME_TYPE_GEOJSON))
				.andReturn();

		assertThat(new JSONObject(rtn.getResponse().getContentAsString()),
				sameJSONObjectAs(new JSONObject(getCompareFile(RESULT_FOLDER, "basin/comid_13302592.json"))).allowingAnyArrayOrdering());
	}

}
