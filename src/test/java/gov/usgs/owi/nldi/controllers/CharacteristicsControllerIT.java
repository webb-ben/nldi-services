package gov.usgs.owi.nldi.controllers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.co.datumedge.hamcrest.json.SameJSONAs.sameJSONObjectAs;

import org.json.JSONObject;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

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
import gov.usgs.owi.nldi.transform.BasinTransformer;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment=WebEnvironment.MOCK,
		classes={DbTestConfig.class, SpringConfig.class, 
		CharacteristicsController.class, LookupDao.class, StreamingDao.class,
		Navigation.class, NavigationDao.class, Parameters.class, 
		ConfigurationService.class, LogService.class, LogDao.class})
@DatabaseSetup("classpath:/testData/crawlerSource.xml")
public class CharacteristicsControllerIT extends BaseIT {

	@Autowired
	private MockMvc mockMvc;

	private static final String RESULT_FOLDER  = "characteristic/";

	@Test
	public void getCharacteristicsTest() throws Exception {
		MvcResult rtn = mockMvc.perform(get("/api/tot/characteristics"))
				.andExpect(status().isOk())
				.andExpect(header().string(NetworkController.HEADER_CONTENT_TYPE, NetworkController.MIME_TYPE_GEOJSON))
				.andReturn();

		assertThat(new JSONObject(rtn.getResponse().getContentAsString()),
				sameJSONObjectAs(new JSONObject(getCompareFile(RESULT_FOLDER, "meta/tot.json"))).allowingAnyArrayOrdering());
	}

	@Test
	public void getCharacteristicDataTest() throws Exception {
		MvcResult rtn = mockMvc.perform(get("/api/comid/13302592/tot"))
				.andExpect(status().isOk())
				.andExpect(header().string(NetworkController.HEADER_CONTENT_TYPE, NetworkController.MIME_TYPE_GEOJSON))
				.andReturn();

		assertThat(new JSONObject(rtn.getResponse().getContentAsString()),
				sameJSONObjectAs(new JSONObject(getCompareFile(RESULT_FOLDER, "data/comid_13302592_tot.json"))).allowingAnyArrayOrdering());
	}

	@Test
	public void getCharacteristicDataFilteredTest() throws Exception {
		MvcResult rtn = mockMvc.perform(get("/api/comid/13302592/tot?characteristicId=TOT_N97&characteristicId=TOT_ET"))
				.andExpect(status().isOk())
				.andExpect(header().string(NetworkController.HEADER_CONTENT_TYPE, NetworkController.MIME_TYPE_GEOJSON))
				.andReturn();

		assertThat(new JSONObject(rtn.getResponse().getContentAsString()),
				sameJSONObjectAs(new JSONObject(getCompareFile(RESULT_FOLDER, "data/comid_13302592_tot_filtered.json"))).allowingAnyArrayOrdering());
	}

	@Test
	public void getBasinTest() throws Exception {
		MvcResult rtn = mockMvc.perform(get("/api/comid/13302592/basin"))
				.andExpect(status().isOk())
				.andExpect(header().string(BasinTransformer.BASIN_COUNT_HEADER, "1"))
				.andExpect(header().string(NetworkController.HEADER_CONTENT_TYPE, NetworkController.MIME_TYPE_GEOJSON))
				.andReturn();

		assertThat(new JSONObject(rtn.getResponse().getContentAsString()),
				sameJSONObjectAs(new JSONObject(getCompareFile(RESULT_FOLDER, "basin/comid_13302592.json"))).allowingAnyArrayOrdering());
	}

}
