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

@Category(FullIntegrationTest.class)
@DatabaseSetup("classpath:/testData/crawlerSource.xml")
public class LinkedDataControllerDataSourceFullIntegrationTest extends BaseSpringTest {

	@Autowired
	private WebApplicationContext wac;

	private MockMvc mockMvc;

	private static final String RESULT_FOLDER_WQP  = "feature/feature/wqp/";
	private static final String RESULT_FOLDER_HUC  = "feature/feature/huc12pp/";

	@Before
	public void setup() {
		mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
	}

	//Linked Object Testing WQP
	@Test
	@DatabaseSetup("classpath:/testData/featureWqp.xml")
	public void getWqpTest() throws Exception {
		MvcResult rtn = mockMvc.perform(get("/wqp/USGS-05427880"))
				.andExpect(status().isOk())
				.andExpect(header().string(NetworkController.HEADER_CONTENT_TYPE, NetworkController.MIME_TYPE_GEOJSON))
				.andReturn();

		assertThat(new JSONObject(rtn.getResponse().getContentAsString()),
				sameJSONObjectAs(new JSONObject(getCompareFile(RESULT_FOLDER_WQP, "wqp_USGS-05427880.geojson"))).allowingAnyArrayOrdering());
	}

	//Linked Object Testing huc12pp
	@Test
	@DatabaseSetup("classpath:/testData/featureHuc12pp.xml")
	public void gethuc12ppTest() throws Exception {
		MvcResult rtn = mockMvc.perform(get("/huc12pp/070900020604"))
				.andExpect(status().isOk())
				.andExpect(header().string(NetworkController.HEADER_CONTENT_TYPE, NetworkController.MIME_TYPE_GEOJSON))
				.andReturn();

		assertThat(new JSONObject(rtn.getResponse().getContentAsString()),
				sameJSONObjectAs(new JSONObject(getCompareFile(RESULT_FOLDER_HUC, "huc12pp_070900020604.geojson"))).allowingAnyArrayOrdering());
	}

	//Navigation Within Datasource Testing
	@Test
	@DatabaseSetup("classpath:/testData/featureWqp.xml")
	public void getWqpUtTest() throws Exception {
		MvcResult rtn = mockMvc.perform(get("/wqp/USGS-05427880/navigate/UT/wqp"))
				.andExpect(status().isOk())
				.andExpect(header().string(NetworkController.FEATURE_COUNT_HEADER, "12"))
				.andExpect(header().string(NetworkController.HEADER_CONTENT_TYPE, NetworkController.MIME_TYPE_GEOJSON))
				.andReturn();

		assertThat(new JSONObject(rtn.getResponse().getContentAsString()),
				sameJSONObjectAs(new JSONObject(getCompareFile(RESULT_FOLDER_WQP, "wqp_USGS-05427880_UT_wqp.geojson"))).allowingAnyArrayOrdering());
	}

	//Navigation Different Datasource Testing
	@Test
	@DatabaseSetup("classpath:/testData/featureHuc12pp.xml")
	@DatabaseSetup("classpath:/testData/featureWqp.xml")
	public void getWqpDmTest() throws Exception {
		MvcResult rtn = mockMvc.perform(get("/wqp/USGS-05427880/navigate/DM/huc12pp"))
				.andExpect(status().isOk())
				.andExpect(header().string(NetworkController.FEATURE_COUNT_HEADER, "9"))
				.andExpect(header().string(NetworkController.HEADER_CONTENT_TYPE, NetworkController.MIME_TYPE_GEOJSON))
				.andReturn();

		assertThat(new JSONObject(rtn.getResponse().getContentAsString()),
				sameJSONObjectAs(new JSONObject(getCompareFile(RESULT_FOLDER_WQP, "wqp_USGS-05427880_DM_huc12pp.geojson"))).allowingAnyArrayOrdering());
	}

	@Test
	public void badInputTest() throws Exception {
		mockMvc.perform(get("/wqx/USGS-05427880/navigate/DM/huc12pp"))
				.andExpect(status().isNotFound());
	}

}
