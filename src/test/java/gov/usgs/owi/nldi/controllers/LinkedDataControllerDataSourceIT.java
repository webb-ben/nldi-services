package gov.usgs.owi.nldi.controllers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.co.datumedge.hamcrest.json.SameJSONAs.sameJSONObjectAs;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import gov.usgs.owi.nldi.BaseIT;
import gov.usgs.owi.nldi.dao.LogDao;
import gov.usgs.owi.nldi.dao.LookupDao;
import gov.usgs.owi.nldi.dao.NavigationDao;
import gov.usgs.owi.nldi.dao.StreamingDao;
import gov.usgs.owi.nldi.services.ConfigurationService;
import gov.usgs.owi.nldi.services.LogService;
import gov.usgs.owi.nldi.services.Navigation;
import gov.usgs.owi.nldi.services.Parameters;
import gov.usgs.owi.nldi.springinit.DbTestConfig;
import gov.usgs.owi.nldi.springinit.SpringConfig;
import gov.usgs.owi.nldi.transform.FeatureTransformer;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment=WebEnvironment.MOCK,
		classes={DbTestConfig.class, SpringConfig.class, 
		LinkedDataController.class, LookupDao.class, StreamingDao.class,
		Navigation.class, NavigationDao.class,Parameters.class, 
		ConfigurationService.class, LogService.class, LogDao.class})
@DatabaseSetup("classpath:/testData/crawlerSource.xml")
public class LinkedDataControllerDataSourceIT extends BaseIT {

	@Autowired
	private WebApplicationContext wac;

	private MockMvc mockMvc;

	@Before
	public void setup() {
		mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
	}

	//Navigation Within Datasource Testing
	@Test
	@DatabaseSetup("classpath:/testData/featureWqp.xml")
	public void getWqpUtTest() throws Exception {
		MvcResult rtn = mockMvc.perform(get("/api/wqp/USGS-05427880/navigate/UT/wqp"))
				.andExpect(status().isOk())
				.andExpect(header().string(FeatureTransformer.FEATURE_COUNT_HEADER, "13"))
				.andExpect(header().string(NetworkController.HEADER_CONTENT_TYPE, NetworkController.MIME_TYPE_GEOJSON))
				.andReturn();

		assertThat(new JSONObject(rtn.getResponse().getContentAsString()),
				sameJSONObjectAs(new JSONObject(getCompareFile(RESULT_FOLDER_WQP, "wqp_USGS-05427880_UT_wqp.json"))).allowingAnyArrayOrdering());
	}

	//Navigation Different Datasource Testing
	@Test
	@DatabaseSetup("classpath:/testData/featureHuc12pp.xml")
	@DatabaseSetup("classpath:/testData/featureWqp.xml")
	public void getWqpDmTest() throws Exception {
		MvcResult rtn = mockMvc.perform(get("/api/wqp/USGS-05427880/navigate/DM/huc12pp"))
				.andExpect(status().isOk())
				.andExpect(header().string(FeatureTransformer.FEATURE_COUNT_HEADER, "9"))
				.andExpect(header().string(NetworkController.HEADER_CONTENT_TYPE, NetworkController.MIME_TYPE_GEOJSON))
				.andReturn();

		assertThat(new JSONObject(rtn.getResponse().getContentAsString()),
				sameJSONObjectAs(new JSONObject(getCompareFile(RESULT_FOLDER_WQP, "wqp_USGS-05427880_DM_huc12pp.json"))).allowingAnyArrayOrdering());
	}

	@Test
	public void badInputTest() throws Exception {
		mockMvc.perform(get("/api/wqx/USGS-05427880/navigate/DM/huc12pp"))
				.andExpect(status().isNotFound());
	}

}
