package gov.usgs.owi.nldi.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import gov.usgs.owi.nldi.dao.CountDao;
import gov.usgs.owi.nldi.dao.LookupDao;
import gov.usgs.owi.nldi.dao.StreamingDao;
import gov.usgs.owi.nldi.services.Navigation;
import gov.usgs.owi.nldi.springinit.TestSpringConfig;

public class LinkedDataControllerTest {

	@Mock
	private CountDao countDao;
	@Mock
	private StreamingDao streamingDao;
	@Mock
	private LookupDao lookupDao;
	@Mock
	private Navigation navigation;
	private LinkedDataController controller;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		controller = new LinkedDataController(countDao, lookupDao, streamingDao, navigation, TestSpringConfig.TEST_ROOT_URL);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void getComidTest() {
		when(lookupDao.getComid(anyString(), anyMap())).thenReturn(goodFeature(), null, missingFeature());

		assertEquals("12345", controller.getComid("abc", "def"));

		assertNull(controller.getComid("abc", "def"));

		assertNull(controller.getComid("abc", "def"));
	}

	public static Map<String, Object> goodFeature() {
		Map<String, Object> rtn = new LinkedHashMap<>();
		rtn.put(Navigation.COMID, "12345");
		return rtn;
	}

	public static Map<String, Object> missingFeature() {
		Map<String, Object> rtn = new LinkedHashMap<>();
		return rtn;
	}

}
