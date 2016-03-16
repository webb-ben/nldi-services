package gov.usgs.owi.nldi.controllers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

import gov.usgs.owi.nldi.BaseSpringTest;
import gov.usgs.owi.nldi.FullIntegrationTest;

@Category(FullIntegrationTest.class)
public class RestControllerFullIntegrationTest extends BaseSpringTest {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;
	
    @Before
    public void setup() {
    	mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }
    
    //UT Testing
	@Test
	public void getComidUtTest() throws Exception {
		MvcResult rtn = mockMvc.perform(get("/comid/13293474/navigate/UT")).andExpect(status().isOk()).andReturn();

		assertThat(new JSONObject(rtn.getResponse().getContentAsString()),
        		sameJSONObjectAs(new JSONObject(getCompareFile("comid_13293474_UT.geojson"))));
	}

	@Test
	public void getComidUtDistanceTest() throws Exception {
		MvcResult rtn = mockMvc.perform(get("/comid/13297246/navigate/UT?distance=10")).andExpect(status().isOk()).andReturn();

		assertThat(new JSONObject(rtn.getResponse().getContentAsString()),
        		sameJSONObjectAs(new JSONObject(getCompareFile("comid_13297246_UT_distance_10.geojson"))));
	}

    //UM Testing
	@Test
	public void getComidUmTest() throws Exception {
		MvcResult rtn = mockMvc.perform(get("/comid/13293474/navigate/UM")).andExpect(status().isOk()).andReturn();

		assertThat(new JSONObject(rtn.getResponse().getContentAsString()),
        		sameJSONObjectAs(new JSONObject(getCompareFile("comid_13293474_UM.geojson"))));
	}

	@Test
	public void getComidUmDistanceTest() throws Exception {
		MvcResult rtn = mockMvc.perform(get("/comid/13297246/navigate/UM?distance=10")).andExpect(status().isOk()).andReturn();

		assertThat(new JSONObject(rtn.getResponse().getContentAsString()),
        		sameJSONObjectAs(new JSONObject(getCompareFile("comid_13297246_UM_distance_10.geojson"))));
	}

    //DM Testing
	@Test
	public void getComidDmTest() throws Exception {
		MvcResult rtn = mockMvc.perform(get("/comid/13296790/navigate/DM")).andExpect(status().isOk()).andReturn();

		assertThat(new JSONObject(rtn.getResponse().getContentAsString()),
        		sameJSONObjectAs(new JSONObject(getCompareFile("comid_13296790_DM.geojson"))));
	}

	@Test
	public void getComidDmDistanceTest() throws Exception {
		MvcResult rtn = mockMvc.perform(get("/comid/13293474/navigate/DM?distance=10")).andExpect(status().isOk()).andReturn();

		assertThat(new JSONObject(rtn.getResponse().getContentAsString()),
        		sameJSONObjectAs(new JSONObject(getCompareFile("comid_13293474_DM_distance_10.geojson"))));
	}

    //DD Testing - Except we really don't have any diversions in the test data...
	@Test
	public void getComidDdTest() throws Exception {
		MvcResult rtn = mockMvc.perform(get("/comid/13297242/navigate/DD")).andExpect(status().isOk()).andReturn();

		assertThat(new JSONObject(rtn.getResponse().getContentAsString()),
        		sameJSONObjectAs(new JSONObject(getCompareFile("comid_13297242_DD.geojson"))));
	}

	@Test
	public void getComidDdDistanceTest() throws Exception {
		MvcResult rtn = mockMvc.perform(get("/comid/13293506/navigate/DD?distance=10")).andExpect(status().isOk()).andReturn();

		assertThat(new JSONObject(rtn.getResponse().getContentAsString()),
        		sameJSONObjectAs(new JSONObject(getCompareFile("comid_13293506_DD_distance_10.geojson"))));
	}

	//PP Testing
	@Test
	public void getComidPpStopComidInvalidTest() throws Exception {
		MvcResult rtn = mockMvc.perform(get("/comid/13297246/navigate/PP?stopComid=13297198")).andExpect(status().isBadRequest()).andReturn();

		assertThat(new JSONObject(rtn.getResponse().getContentAsString()),
        		sameJSONObjectAs(new JSONObject(getCompareFile("comid_13297246_PP_stop_13297198.geojson"))));
	}

	@Test
	public void getComidPpStopComidTest() throws Exception {
		MvcResult rtn = mockMvc.perform(get("/comid/13297198/navigate/PP?stopComid=13297246")).andExpect(status().isOk()).andReturn();

		assertThat(new JSONObject(rtn.getResponse().getContentAsString()),
        		sameJSONObjectAs(new JSONObject(getCompareFile("comid_13297198_PP_stop_13297246.geojson"))));
	}

}
