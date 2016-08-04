package gov.usgs.owi.nldi.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Map;

import org.junit.Test;

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

}
