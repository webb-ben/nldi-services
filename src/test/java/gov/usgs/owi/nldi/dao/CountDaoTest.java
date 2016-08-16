package gov.usgs.owi.nldi.dao;

import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

import gov.usgs.owi.nldi.BaseSpringTest;
import gov.usgs.owi.nldi.DBIntegrationTest;
import gov.usgs.owi.nldi.NavigationMode;
import gov.usgs.owi.nldi.controllers.NetworkController;
import gov.usgs.owi.nldi.services.Parameters;

@Category(DBIntegrationTest.class)
public class CountDaoTest extends BaseSpringTest {

	@Autowired
	CountDao countDao;
	
	@Test
	public void countFlowLinesTest() {
		//TODO - Real verification - this test just validates that the query has no syntax errors, not that it is logically correct.
		Map<String, Object> parameterMap = new HashMap<>();

		//No limits
		parameterMap.put(Parameters.NAVIGATION_MODE, NavigationMode.DM.toString());
		assertNotNull(countDao.count(BaseDao.FLOW_LINES, parameterMap));

		parameterMap.put(Parameters.NAVIGATION_MODE, NavigationMode.UM.toString());
		assertNotNull(countDao.count(BaseDao.FLOW_LINES, parameterMap));

		parameterMap.put(Parameters.NAVIGATION_MODE, NavigationMode.UT.toString());
		assertNotNull(countDao.count(BaseDao.FLOW_LINES, parameterMap));

		//With distance
		parameterMap.put(Parameters.DISTANCE, 5);
		parameterMap.put(Parameters.NAVIGATION_MODE, NavigationMode.DM.toString());
		assertNotNull(countDao.count(BaseDao.FLOW_LINES, parameterMap));

		parameterMap.put(Parameters.NAVIGATION_MODE, NavigationMode.UM.toString());
		assertNotNull(countDao.count(BaseDao.FLOW_LINES, parameterMap));

		parameterMap.put(Parameters.NAVIGATION_MODE, NavigationMode.UT.toString());
		assertNotNull(countDao.count(BaseDao.FLOW_LINES, parameterMap));
	}

	@Test
	public void countFeaturesTest() {
		//TODO - Real verification - this test just validates that the query has no syntax errors, not that it is logically correct.
		Map<String, Object> parameterMap = new HashMap<>();
		parameterMap.put(NetworkController.DATA_SOURCE, "wqp");

		//No limits
		parameterMap.put(Parameters.NAVIGATION_MODE, NavigationMode.DM.toString());
		assertNotNull(countDao.count(BaseDao.FEATURES, parameterMap));

		parameterMap.put(Parameters.NAVIGATION_MODE, NavigationMode.UM.toString());
		assertNotNull(countDao.count(BaseDao.FEATURES, parameterMap));

		parameterMap.put(Parameters.NAVIGATION_MODE, NavigationMode.UT.toString());
		assertNotNull(countDao.count(BaseDao.FEATURES, parameterMap));

		//With distance
		parameterMap.put(Parameters.DISTANCE, 5);
		parameterMap.put(Parameters.NAVIGATION_MODE, NavigationMode.DM.toString());
		assertNotNull(countDao.count(BaseDao.FEATURES, parameterMap));

		parameterMap.put(Parameters.NAVIGATION_MODE, NavigationMode.UM.toString());
		assertNotNull(countDao.count(BaseDao.FEATURES, parameterMap));

		parameterMap.put(Parameters.NAVIGATION_MODE, NavigationMode.UT.toString());
		assertNotNull(countDao.count(BaseDao.FEATURES, parameterMap));
	}

}
