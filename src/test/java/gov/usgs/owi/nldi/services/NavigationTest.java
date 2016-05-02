package gov.usgs.owi.nldi.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import gov.usgs.owi.nldi.dao.NavigationDao;

public class NavigationTest {

	@Mock
	private NavigationDao navigationDao;

	private Navigation navigation;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		navigation = new Navigation(navigationDao);
	}

	@Test
	public void processParametersTest() {
		assertTrue(navigation.processParameters(null, null, null, null).isEmpty());
		
		assertTrue(navigation.processParameters("", "", "", "").isEmpty());
		
		assertTrue(navigation.processParameters(" ", " ", " ", " ").isEmpty());

		Map<String, Object> parameterMap = navigation.processParameters("123", "DD", "456", "789");

		assertEquals(4, parameterMap.size());
		assertTrue(parameterMap.containsKey(Navigation.COMID));
		assertEquals(123, parameterMap.get(Navigation.COMID));
		assertTrue(parameterMap.containsKey(Navigation.NAVIGATION_MODE));
		assertEquals("DD", parameterMap.get(Navigation.NAVIGATION_MODE));
		assertTrue(parameterMap.containsKey(Navigation.DISTANCE));
		assertEquals(BigDecimal.valueOf(456), parameterMap.get(Navigation.DISTANCE));
		assertTrue(parameterMap.containsKey(Navigation.STOP_COMID));
		assertEquals(789, parameterMap.get(Navigation.STOP_COMID));
	}

	@Test
	public void interpretResultTest() {
		OutputStream baos = new ByteArrayOutputStream();
		assertEquals("{4d06cca2-001e-11e6-b9d0-0242ac110003}", navigation.interpretResult(null, goodResult()));

		assertNull(navigation.interpretResult(null, badResult1()));

		assertNull(navigation.interpretResult(baos, badResult1()));
		assertEquals("{\"errorCode\":-1, \"errorMessage\":\"Valid navigation type codes are UM, UT, DM, DD and PP.\"}", baos.toString());

		baos = new ByteArrayOutputStream();
		assertNull(navigation.interpretResult(baos, badResult2()));
		assertEquals("{\"errorCode\":310, \"errorMessage\":\"Start ComID must have a hydroseq greater than the hydroseq for stop ComID.\"}", baos.toString());
	}

	@Test
	@SuppressWarnings("unchecked")
	public void navigateTest() {
		when(navigationDao.navigate(anyMap())).thenReturn(goodResult(), badResult1());
		OutputStream baos = new ByteArrayOutputStream();
		assertEquals("{4d06cca2-001e-11e6-b9d0-0242ac110003}", navigation.navigate(baos, null, null, null, null));

		assertNull(navigation.navigate(baos, null, null, null, null));
		assertEquals("{\"errorCode\":-1, \"errorMessage\":\"Valid navigation type codes are UM, UT, DM, DD and PP.\"}", baos.toString());
	}

	protected Map<String, String> goodResult() {
		Map<String,String> navigationResult = new LinkedHashMap<>();
		navigationResult.put(NavigationDao.NAVIGATE_CACHED, "(13297246,0.0000000000,,,0,,{4d06cca2-001e-11e6-b9d0-0242ac110003})");
		return navigationResult;
	}

	protected Map<String, String> badResult1() {
		Map<String,String> navigationResult = new LinkedHashMap<>();
		navigationResult.put(NavigationDao.NAVIGATE_CACHED, "(,,,,-1,\"Valid navigation type codes are UM, UT, DM, DD and PP.\",)");
		return navigationResult;
	}

	protected Map<String, String> badResult2() {
		Map<String,String> navigationResult = new LinkedHashMap<>();
		navigationResult.put(NavigationDao.NAVIGATE_CACHED, "13297246,1.1545800000,13297198,48.5846800000,310,\"Start ComID must have a hydroseq greater than the hydroseq for stop ComID.\",{f170f490-00ad-11e6-8f62-0242ac110003})");
		return navigationResult;
	}

}
