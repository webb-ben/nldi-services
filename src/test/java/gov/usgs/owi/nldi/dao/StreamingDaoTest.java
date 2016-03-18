package gov.usgs.owi.nldi.dao;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.session.ResultContext;
import org.apache.ibatis.session.ResultHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.NumberUtils;

import gov.usgs.owi.nldi.BaseSpringTest;
import gov.usgs.owi.nldi.DBIntegrationTest;
import gov.usgs.owi.nldi.controllers.RestController;

@Category(DBIntegrationTest.class)
public class StreamingDaoTest extends BaseSpringTest {

	@Autowired 
	StreamingDao streamingDao;
	
	private class TestResultHandler implements ResultHandler<Object> {
		//TODO put the results somewhere to check them and allow them to be cleared between queries
//		public ArrayList<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		@Override
		public void handleResult(ResultContext<?> context) {
//			results.add((Map<String, Object>) context.getResultObject());
		}
	}

	TestResultHandler handler;

	@Before
	public void init() {
		handler = new TestResultHandler();
	}

	@After
	public void cleanup() {
		handler = null;
	}

	@Test
	public void navigateTest() {
		//TODO - Real verification - this test just validates that the query has no syntax errors, not that it is logically correct.
		Map<String, Object> parameterMap = new HashMap<>();
		parameterMap.put(RestController.COMID, NumberUtils.parseNumber("1329374", Integer.class));
		parameterMap.put(RestController.NAVIGATION_MODE, "UT");
		parameterMap.put(RestController.STOP_COMID, NumberUtils.parseNumber("13297246", Integer.class));
		parameterMap.put(RestController.DISTANCE, NumberUtils.parseNumber("10", BigDecimal.class));

		assertNotNull(streamingDao.navigate(RestController.NAVIGATE, parameterMap));
	}

	@Test
	public void streamTest() {
		//TODO - Real verification - this test just validates that the query has no syntax errors, not that it is logically correct.
		Map<String, Object> parameterMap = new HashMap<>();

		//MyBatis is happy with no parms or ResultHandler - it will read the entire database, load up the list,
		// and not complain or expose it to you (unless you run out of memory). We have a check to make sure the 
		// resultHandler is not null. (The tests were failing on Jenkins with "java.lang.OutOfMemoryError: Java heap space")
		try {
			streamingDao.stream(null, null, null);
		} catch (RuntimeException e) {
			if (!"A ResultHandler is required for the StreamingDao.stream".equalsIgnoreCase(e.getMessage())) {
				fail("Expected a RuntimeException, but got " + e.getLocalizedMessage());
			}
		}
		try {
		streamingDao.stream(RestController.NAVIGATE, null, null);
		} catch (RuntimeException e) {
			if (!"A ResultHandler is required for the StreamingDao.stream".equalsIgnoreCase(e.getMessage())) {
				fail("Expected a RuntimeException, but got " + e.getLocalizedMessage());
			}
		}
		try {
		streamingDao.stream(RestController.NAVIGATE, parameterMap, null);
		} catch (RuntimeException e) {
			if (!"A ResultHandler is required for the StreamingDao.stream".equalsIgnoreCase(e.getMessage())) {
				fail("Expected a RuntimeException, but got " + e.getLocalizedMessage());
			}
		}
		
		streamingDao.stream(RestController.NAVIGATE, parameterMap, handler);
		
		parameterMap.put(RestController.SESSION_ID, "abc");
		streamingDao.stream(RestController.NAVIGATE, parameterMap, handler);
		
	}
}