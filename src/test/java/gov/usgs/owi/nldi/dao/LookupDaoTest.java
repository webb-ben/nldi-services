package gov.usgs.owi.nldi.dao;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static uk.co.datumedge.hamcrest.json.SameJSONAs.sameJSONObjectAs;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import gov.usgs.owi.nldi.BaseSpringTest;
import gov.usgs.owi.nldi.DBIntegrationTest;
import gov.usgs.owi.nldi.controllers.LinkedDataController;
import gov.usgs.owi.nldi.controllers.LookupController;
import gov.usgs.owi.nldi.springinit.TestSpringConfig;
import gov.usgs.owi.nldi.transform.FeatureTransformer;

@Category(DBIntegrationTest.class)
@DatabaseSetup("classpath:/testData/crawlerSource.xml")
public class LookupDaoTest extends BaseSpringTest {

	@Autowired
	LookupDao lookupDao;

	@Test
	public void getFeatureTest() throws IOException, JSONException {
		Map<String, Object> parameterMap = new HashMap<>();
		parameterMap.put(LinkedDataController.FEATURE_SOURCE, "wqp");
		parameterMap.put(LinkedDataController.FEATURE_ID, "USGS-05427880");
		Map<String, Object> results = lookupDao.getOne(BaseDao.FEATURE, parameterMap);
		assertEquals("Water Quality Portal", results.get(FeatureTransformer.SOURCE_NAME_DB));
		assertEquals("USGS-05427880", results.get(FeatureTransformer.IDENTIFIER));
		assertEquals("SIXMILE CREEK AT STATE HIGHWAY 19 NEAR WAUNAKEE,WI", results.get(FeatureTransformer.NAME));
		assertEquals("http://www.waterqualitydata.us/provider/NWIS/USGS-WI/USGS-05427880/", results.get(FeatureTransformer.URI));
		assertEquals(13294132, results.get(FeatureTransformer.COMID));
		assertNull(results.get(FeatureTransformer.REACHCODE));
		assertNull(results.get(FeatureTransformer.MEASURE));
		assertThat(new JSONObject("{\"type\": \"Point\", \"coordinates\": [-89.4728889, 43.1922222]}"),
				sameJSONObjectAs(new JSONObject(results.get("shape").toString())).allowingAnyArrayOrdering());
	}

	@Test
	public void getDataSourcesTest() {
		Map<String, Object> parameterMap = new HashMap<>();
		parameterMap.put(LookupController.ROOT_URL, TestSpringConfig.TEST_ROOT_URL);
		List<Map<String, Object>> results = lookupDao.getList(BaseDao.DATA_SOURCES, parameterMap);
		assertFalse(results.isEmpty());
		assertEquals(4, results.size());
		assertEquals("huc12pp", results.get(0).get(FeatureTransformer.SOURCE));
		assertEquals("huc12pp", results.get(0).get(FeatureTransformer.SOURCE_NAME));
		assertEquals(String.join("/", TestSpringConfig.TEST_ROOT_URL, "huc12pp"), results.get(0).get(BaseDao.FEATURES));
		assertEquals("NWIS", results.get(1).get(FeatureTransformer.SOURCE));
		assertEquals("USGS_NHD_StreamGages_ActiveContinuous", results.get(1).get(FeatureTransformer.SOURCE_NAME));
		assertEquals(String.join("/", TestSpringConfig.TEST_ROOT_URL, "NWIS"), results.get(1).get(BaseDao.FEATURES));
		assertEquals("TEST", results.get(2).get(FeatureTransformer.SOURCE));
		assertEquals("TEST Source", results.get(2).get(FeatureTransformer.SOURCE_NAME));
		assertEquals(String.join("/", TestSpringConfig.TEST_ROOT_URL, "TEST"), results.get(2).get(BaseDao.FEATURES));
		assertEquals("WQP", results.get(3).get(FeatureTransformer.SOURCE));
		assertEquals("Water Quality Portal", results.get(3).get(FeatureTransformer.SOURCE_NAME));
		assertEquals(String.join("/", TestSpringConfig.TEST_ROOT_URL, "WQP"), results.get(3).get(BaseDao.FEATURES));
	}
}
