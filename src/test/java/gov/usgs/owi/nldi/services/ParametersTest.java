package gov.usgs.owi.nldi.services;

import java.math.BigDecimal;
import java.util.Map;


import gov.usgs.owi.nldi.NavigationMode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;


public class ParametersTest {

	private Parameters parameters = new Parameters();

	@Test
	public void processParametersTest() {
		assertTrue(parameters.processParameters(null, null, null, null).isEmpty());

		assertTrue(parameters.processParameters("", "", "", "").isEmpty());

		assertTrue(parameters.processParameters(" ", " ", " ", " ").isEmpty());

		Map<String, Object> parameterMap = parameters.processParameters("123", "DD", "456", "789");

		assertEquals(4, parameterMap.size());
		assertTrue(parameterMap.containsKey(Parameters.COMID));
		assertEquals(123, parameterMap.get(Parameters.COMID));
		assertTrue(parameterMap.containsKey(Parameters.NAVIGATION_MODE));
		assertEquals("DD", parameterMap.get(Parameters.NAVIGATION_MODE));
		assertTrue(parameterMap.containsKey(Parameters.DISTANCE));
		assertEquals(BigDecimal.valueOf(456), parameterMap.get(Parameters.DISTANCE));
		assertTrue(parameterMap.containsKey(Parameters.STOP_COMID));
		assertEquals(789, parameterMap.get(Parameters.STOP_COMID));
	}

	@Test
	public void processParametersOverloadTest() {
		assertTrue(parameters.processParameters(null, null, null, null, null,null).isEmpty());

		assertTrue(parameters.processParameters("", "", "", "", "", "").isEmpty());

		assertTrue(parameters.processParameters(" ", " ", " ", " ", " ", " ").isEmpty());

		Map<String, Object> parameterMap = parameters.processParameters("123", "DD", "456", "789", "29.86", "3");

		assertEquals(6, parameterMap.size());
		assertTrue(parameterMap.containsKey(Parameters.COMID));
		assertEquals(123, parameterMap.get(Parameters.COMID));
		assertTrue(parameterMap.containsKey(Parameters.NAVIGATION_MODE));
		assertEquals("DD", parameterMap.get(Parameters.NAVIGATION_MODE));
		assertTrue(parameterMap.containsKey(Parameters.DISTANCE));
		assertEquals(BigDecimal.valueOf(456), parameterMap.get(Parameters.DISTANCE));
		assertTrue(parameterMap.containsKey(Parameters.STOP_COMID));
		assertEquals(789, parameterMap.get(Parameters.STOP_COMID));
		assertTrue(parameterMap.containsKey(Parameters.MEASURE));
		assertEquals(Float.parseFloat("29.86"), parameterMap.get(Parameters.MEASURE));
		assertTrue(parameterMap.containsKey(Parameters.TRIM_TOLERANCE));
		assertEquals(Float.parseFloat("3.0"), parameterMap.get(Parameters.TRIM_TOLERANCE));
	}

	@Test
	public void validateComidTest() {
		assertNull(parameters.validateComid(null, true));
		assertNull(parameters.validateComid("", true));
		assertNull(parameters.validateComid(" ", false));
		assertNull(parameters.validateComid("abc", false));
		assertEquals(Integer.valueOf(123), parameters.validateComid("123", false));
		assertEquals(Integer.valueOf(123), parameters.validateComid(" 123 ", false));
		assertEquals(Integer.valueOf(123), parameters.validateComid("1 23", false));
	}

	@Test
	public void isValidNavigationModeTest() {
		assertFalse(parameters.isValidNavigationMode(null));
		assertFalse(parameters.isValidNavigationMode(""));
		assertFalse(parameters.isValidNavigationMode(" "));
		assertFalse(parameters.isValidNavigationMode("1"));
		assertFalse(parameters.isValidNavigationMode("dd"));
		assertTrue(parameters.isValidNavigationMode(NavigationMode.DD.toString()));
		assertTrue(parameters.isValidNavigationMode(NavigationMode.DM.toString()));
		assertTrue(parameters.isValidNavigationMode(NavigationMode.UM.toString()));
		assertTrue(parameters.isValidNavigationMode(NavigationMode.UT.toString()));
	}

}
