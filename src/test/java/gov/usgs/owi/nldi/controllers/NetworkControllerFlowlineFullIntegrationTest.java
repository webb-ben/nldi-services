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
public class NetworkControllerFlowlineFullIntegrationTest extends BaseSpringTest {

	@Autowired
	private WebApplicationContext wac;

	private MockMvc mockMvc;

	private static final String RESULT_FOLDER  = "network/flowline/";

	@Before
	public void setup() {
		mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
	}

	//UT Testing
	@Test
	public void getComidUtTest() throws Exception {
		MvcResult rtn = mockMvc.perform(get("/comid/13293474/navigate/UT"))
				.andExpect(status().isOk())
				.andExpect(header().string(NetworkController.FLOW_LINES_COUNT_HEADER, "7"))
				.andExpect(header().string(NetworkController.HEADER_CONTENT_TYPE, NetworkController.MIME_TYPE_GEOJSON))
				.andReturn();

		assertThat(new JSONObject(rtn.getResponse().getContentAsString()),
				sameJSONObjectAs(new JSONObject(getCompareFile(RESULT_FOLDER, "comid_13293474_UT.geojson"))).allowingAnyArrayOrdering());
	}

	@Test
	public void getComidUtDistanceTest() throws Exception {
		MvcResult rtn = mockMvc.perform(get("/comid/13297246/navigate/UT?distance=10"))
				.andExpect(status().isOk())
				.andExpect(header().string(NetworkController.FLOW_LINES_COUNT_HEADER, "9"))
				.andExpect(header().string(NetworkController.HEADER_CONTENT_TYPE, NetworkController.MIME_TYPE_GEOJSON))
				.andReturn();

		assertThat(new JSONObject(rtn.getResponse().getContentAsString()),
				sameJSONObjectAs(new JSONObject(getCompareFile(RESULT_FOLDER, "comid_13297246_UT_distance_10.geojson"))).allowingAnyArrayOrdering());
	}

	//UM Testing
	@Test
	public void getComidUmTest() throws Exception {
		MvcResult rtn = mockMvc.perform(get("/comid/13293474/navigate/UM"))
				.andExpect(status().isOk())
				.andExpect(header().string(NetworkController.FLOW_LINES_COUNT_HEADER, "4"))
				.andExpect(header().string(NetworkController.HEADER_CONTENT_TYPE, NetworkController.MIME_TYPE_GEOJSON))
				 .andReturn();

		assertThat(new JSONObject(rtn.getResponse().getContentAsString()),
				sameJSONObjectAs(new JSONObject(getCompareFile(RESULT_FOLDER, "comid_13293474_UM.geojson"))).allowingAnyArrayOrdering());
	}

	@Test
	public void getComidUmDistanceTest() throws Exception {
		MvcResult rtn = mockMvc.perform(get("/comid/13297246/navigate/UM?distance=10"))
				.andExpect(status().isOk())
				.andExpect(header().string(NetworkController.FLOW_LINES_COUNT_HEADER, "6"))
				.andExpect(header().string(NetworkController.HEADER_CONTENT_TYPE, NetworkController.MIME_TYPE_GEOJSON))
				.andReturn();

		assertThat(new JSONObject(rtn.getResponse().getContentAsString()),
				sameJSONObjectAs(new JSONObject(getCompareFile(RESULT_FOLDER, "comid_13297246_UM_distance_10.geojson"))).allowingAnyArrayOrdering());
	}

	//DM Testing
	@Test
	public void getComidDmTest() throws Exception {
		MvcResult rtn = mockMvc.perform(get("/comid/13296790/navigate/DM"))
				.andExpect(status().isOk())
				.andExpect(header().string(NetworkController.FLOW_LINES_COUNT_HEADER, "5"))
				.andExpect(header().string(NetworkController.HEADER_CONTENT_TYPE, NetworkController.MIME_TYPE_GEOJSON))
				.andReturn();

		assertThat(new JSONObject(rtn.getResponse().getContentAsString()),
				sameJSONObjectAs(new JSONObject(getCompareFile(RESULT_FOLDER, "comid_13296790_DM.geojson"))).allowingAnyArrayOrdering());
	}

	public void getComidDmDiversionsNotIncludedTest() throws Exception {
		MvcResult rtn = mockMvc.perform(get("/comid/13294310/navigate/DM"))
				.andExpect(status().isOk())
				.andExpect(header().string(NetworkController.FLOW_LINES_COUNT_HEADER, "5"))
				.andExpect(header().string(NetworkController.HEADER_CONTENT_TYPE, NetworkController.MIME_TYPE_GEOJSON))
				.andReturn();

		assertThat(new JSONObject(rtn.getResponse().getContentAsString()),
				sameJSONObjectAs(new JSONObject(getCompareFile(RESULT_FOLDER, "comid_13294310_DM.geojson"))).allowingAnyArrayOrdering());
	}

	@Test
	public void getComidDmDistanceTest() throws Exception {
		MvcResult rtn = mockMvc.perform(get("/comid/13293474/navigate/DM?distance=10"))
				.andExpect(status().isOk())
				.andExpect(header().string(NetworkController.FLOW_LINES_COUNT_HEADER, "8"))
				.andExpect(header().string(NetworkController.HEADER_CONTENT_TYPE, NetworkController.MIME_TYPE_GEOJSON))
				.andReturn();

		assertThat(new JSONObject(rtn.getResponse().getContentAsString()),
				sameJSONObjectAs(new JSONObject(getCompareFile(RESULT_FOLDER, "comid_13293474_DM_distance_10.geojson"))).allowingAnyArrayOrdering());
	}

	//DD Testing
	@Test
	public void getComidDdTest() throws Exception {
		MvcResult rtn = mockMvc.perform(get("/comid/13294310/navigate/DD"))
				.andExpect(status().isOk())
				.andExpect(header().string(NetworkController.FLOW_LINES_COUNT_HEADER, "49"))
				.andExpect(header().string(NetworkController.HEADER_CONTENT_TYPE, NetworkController.MIME_TYPE_GEOJSON))
				.andReturn();

		assertThat(new JSONObject(rtn.getResponse().getContentAsString()),
				sameJSONObjectAs(new JSONObject(getCompareFile(RESULT_FOLDER, "comid_13294310_DD.geojson"))).allowingAnyArrayOrdering());
	}

	@Test
	public void getComidDdDistanceTest() throws Exception {
		MvcResult rtn = mockMvc.perform(get("/comid/13294310/navigate/DD?distance=11"))
				.andExpect(status().isOk())
				.andExpect(header().string(NetworkController.FLOW_LINES_COUNT_HEADER, "11"))
				.andExpect(header().string(NetworkController.HEADER_CONTENT_TYPE, NetworkController.MIME_TYPE_GEOJSON))
				.andReturn();

		assertThat(new JSONObject(rtn.getResponse().getContentAsString()),
				sameJSONObjectAs(new JSONObject(getCompareFile(RESULT_FOLDER, "comid_13294310_DD_distance_11.geojson"))).allowingAnyArrayOrdering());
	}

	//PP Testing
	@Test
	public void getComidPpStopComidInvalidTest() throws Exception {
		MvcResult rtn = mockMvc.perform(get("/comid/13297246/navigate/PP?stopComid=13297198"))
				.andExpect(status().isBadRequest())
				.andExpect(header().string(NetworkController.FLOW_LINES_COUNT_HEADER, (String)null))
				.andExpect(header().string(NetworkController.HEADER_CONTENT_TYPE, (String)null))
				.andReturn();

		assertThat(new JSONObject(rtn.getResponse().getContentAsString()),
				sameJSONObjectAs(new JSONObject(getCompareFile(RESULT_FOLDER, "comid_13297246_PP_stop_13297198.geojson"))).allowingAnyArrayOrdering());
	}

	@Test
	public void getComidPpStopComidTest() throws Exception {
		MvcResult rtn = mockMvc.perform(get("/comid/13297198/navigate/PP?stopComid=13297246"))
				.andExpect(status().isOk())
				.andExpect(header().string(NetworkController.FLOW_LINES_COUNT_HEADER, "12"))
				.andExpect(header().string(NetworkController.HEADER_CONTENT_TYPE, NetworkController.MIME_TYPE_GEOJSON))
				.andReturn();

		assertThat(new JSONObject(rtn.getResponse().getContentAsString()),
				sameJSONObjectAs(new JSONObject(getCompareFile(RESULT_FOLDER, "comid_13297198_PP_stop_13297246.geojson"))).allowingAnyArrayOrdering());
	}

}
