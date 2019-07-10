package gov.usgs.owi.nldi.controllers;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.math.BigInteger;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import gov.usgs.owi.nldi.dao.LookupDao;
import gov.usgs.owi.nldi.dao.StreamingDao;
import gov.usgs.owi.nldi.services.LogService;
import gov.usgs.owi.nldi.services.Navigation;
import gov.usgs.owi.nldi.services.Parameters;
import gov.usgs.owi.nldi.services.TestConfigurationService;


public class CharacteristicsControllerTest {

	private TestConfigurationService configurationService;

	private StreamingDao streamingDao;
	@Mock
	private LookupDao lookupDao;
	@Mock
	private Navigation navigation;
	@Mock
	private Parameters parameters;
	@Mock
	private LogService logService;
	private CharacteristicsController controller;
	private MockHttpServletResponse response;
	private MockHttpServletRequest request;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		configurationService = new TestConfigurationService();
		controller = new CharacteristicsController(lookupDao, streamingDao, navigation, parameters, configurationService, logService);
		response = new MockHttpServletResponse();
		request = new MockHttpServletRequest();

		when(logService.logRequest(any(HttpServletRequest.class))).thenReturn(BigInteger.ONE);
	}

	@Test
	public void getCharacteristicsTest() throws Exception {
		controller.getCharacteristics(request, response, null);
		verify(logService).logRequest(any(HttpServletRequest.class));
		verify(logService).logRequestComplete(any(BigInteger.class), any(int.class));
		//this is a INTERNAL_SERVER_ERROR because of NPEs that shouldn't happen in real life.
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getStatus());
	}

	@Test
	public void getCharacteristicDataTest() throws IOException {
		controller.getCharacteristicData(request, response, null, null, null, null);
		verify(logService).logRequest(any(HttpServletRequest.class));
		verify(logService).logRequestComplete(any(BigInteger.class), any(int.class));
		//this is a INTERNAL_SERVER_ERROR because of NPEs that shouldn't happen in real life.
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getStatus());
	}

	@Test
	public void getBasinTest() throws Exception {
		controller.getBasin(request, response, null, null);
		verify(logService).logRequest(any(HttpServletRequest.class));
		verify(logService).logRequestComplete(any(BigInteger.class), any(int.class));
		//this is a INTERNAL_SERVER_ERROR because of NPEs that shouldn't happen in real life.
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getStatus());
	}

}
