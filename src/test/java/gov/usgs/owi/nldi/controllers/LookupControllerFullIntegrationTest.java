package gov.usgs.owi.nldi.controllers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.co.datumedge.hamcrest.json.SameJSONAs.sameJSONObjectAs;
import static uk.co.datumedge.hamcrest.json.SameJSONAs.sameJSONArrayAs;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import gov.usgs.owi.nldi.BaseSpringTest;
import gov.usgs.owi.nldi.FullIntegrationTest;

@Category(FullIntegrationTest.class)
@DatabaseSetup("classpath:/testData/crawlerSource.xml")
public class LookupControllerFullIntegrationTest extends BaseSpringTest {

	@Autowired
	private WebApplicationContext wac;

	private MockMvc mockMvc;

	private static final String RESULT_FOLDER  = "lookup/";

	@Before
	public void setup() {
		mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
	}

	//DataSources Testing
	@Test
	public void getDataSourcesTest() throws Exception {
		MvcResult rtn = mockMvc.perform(get("/"))
				.andExpect(status().isOk())
				.andExpect(header().string(NetworkController.HEADER_CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
				.andReturn();

		assertThat(new JSONArray(rtn.getResponse().getContentAsString()),
				sameJSONArrayAs(new JSONArray(getCompareFile(RESULT_FOLDER, "dataSources.json"))).allowingAnyArrayOrdering());
	}

	//Navigation Types Testing
	@Test
	public void getNavigationTypesTest() throws Exception {
		MvcResult rtn = mockMvc.perform(get("/wqp/USGS-05427880/navigate"))
				.andExpect(status().isOk())
				.andExpect(header().string(NetworkController.HEADER_CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
				.andReturn();

		assertThat(new JSONObject(rtn.getResponse().getContentAsString()),
				sameJSONObjectAs(new JSONObject(getCompareFile(RESULT_FOLDER, "wqp_USGS-05427880.json"))).allowingAnyArrayOrdering());
	}

	@Test
	public void getNavigationTypesNotFoundTest() throws Exception {
		mockMvc.perform(get("/wqx/USGS-05427880/navigate"))
				.andExpect(status().isNotFound())
				.andExpect(header().string(NetworkController.HEADER_CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

		mockMvc.perform(get("/wqp/USGX-05427880/navigate"))
				.andExpect(status().isNotFound())
				.andExpect(header().string(NetworkController.HEADER_CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
				.andReturn();
	}

}
