package gov.usgs.owi.nldi.dao;

import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

import gov.usgs.owi.nldi.BaseSpringTest;
import gov.usgs.owi.nldi.DBIntegrationTest;
import gov.usgs.owi.nldi.controllers.NetworkController;

@Category(DBIntegrationTest.class)
public class CountDaoTest extends BaseSpringTest {

	@Autowired
	CountDao countDao;
	
	@Test
	public void countFlowLinesTest() {
		//TODO - Real verification - this test just validates that the query has no syntax errors, not that it is logically correct.
		Map<String, Object> parameterMap = new HashMap<>();
		parameterMap.put(NetworkController.SESSION_ID, "{abc}");

		assertNotNull(countDao.count(BaseDao.FLOW_LINES, parameterMap));
	}

	@Test
	public void countFeaturesTest() {
		//TODO - Real verification - this test just validates that the query has no syntax errors, not that it is logically correct.
		Map<String, Object> parameterMap = new HashMap<>();
		parameterMap.put(NetworkController.SESSION_ID, "{abc}");
		parameterMap.put(NetworkController.DATA_SOURCE, "wqp");

		assertNotNull(countDao.count(BaseDao.FEATURES, parameterMap));
	}

}
