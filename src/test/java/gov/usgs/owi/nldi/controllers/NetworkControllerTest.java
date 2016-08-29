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
import gov.usgs.owi.nldi.springinit.TestSpringConfig;

public class NetworkControllerTest {

	private StreamingDao streamingDao;
	@Mock
	private LookupDao lookupDao;
	@Mock
	private Navigation navigation;
	@Mock
	private Parameters parameters;
	@Mock
	private LogService logService;
	private NetworkController controller;
	private MockHttpServletResponse response;
	private MockHttpServletRequest request;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		controller = new NetworkController(lookupDao, streamingDao, navigation, parameters, TestSpringConfig.TEST_ROOT_URL, logService);
		response = new MockHttpServletResponse();
		request = new MockHttpServletRequest();

		when(logService.logRequest(any(HttpServletRequest.class))).thenReturn(BigInteger.ONE);
	}

	@Test
	public void getFlowlinesTest() throws IOException {
		controller.getFlowlines(request, response, null, null, null, null, null);
		verify(logService).logRequest(any(HttpServletRequest.class));
		verify(logService).logRequestComplete(any(BigInteger.class), any(int.class));
		//this is a BAD_REQUEST because the BaseController.streamFeatures dependencies are not all mocked
		assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
	}

	@Test
	public void getFeaturesTest() throws IOException {
		controller.getFeatures(request, response, null, null, null, null, null, null);
		verify(logService).logRequest(any(HttpServletRequest.class));
		verify(logService).logRequestComplete(any(BigInteger.class), any(int.class));
		//this is a BAD_REQUEST because the BaseController.streamFeatures dependencies are not all mocked
		assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
	}

}
