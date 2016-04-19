package gov.usgs.owi.nldi.dao;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import gov.usgs.owi.nldi.BaseSpringTest;
import gov.usgs.owi.nldi.DBIntegrationTest;
import gov.usgs.owi.nldi.services.Navigation;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.NumberUtils;

@Category(DBIntegrationTest.class)
public class NavigationDaoTest extends BaseSpringTest {

	@Autowired 
	NavigationDao navigationDao;
	
	@Test
	public void navigateTest() {
		//TODO - Real verification - this test just validates that the query has no syntax errors, not that it is logically correct.
		Map<String, Object> parameterMap = new HashMap<>();
		parameterMap.put(Navigation.COMID, NumberUtils.parseNumber("1329374", Integer.class));
		parameterMap.put(Navigation.NAVIGATION_MODE, "UT");
		parameterMap.put(Navigation.STOP_COMID, NumberUtils.parseNumber("13297246", Integer.class));
		parameterMap.put(Navigation.DISTANCE, NumberUtils.parseNumber("10", BigDecimal.class));

		assertNotNull(navigationDao.navigate(parameterMap));
	}

	@Test
	public void splitMe() {
		Map<String, Object> parameterMap = new HashMap<>();
		parameterMap.put(Navigation.COMID, NumberUtils.parseNumber("1329374", Integer.class));
		parameterMap.put(Navigation.NAVIGATION_MODE, "UT");

		String sessionId = navigationDao.getCache(parameterMap);
		assertNull(sessionId);

		sessionId = navigationDao.generateSessionId();
		assertNotNull(sessionId);
		
		parameterMap.put("sessionId", sessionId);
		navigationDao.insertCache(parameterMap);

		sessionId = navigationDao.getCache(parameterMap);
	}

}