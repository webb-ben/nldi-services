package gov.usgs.owi.nldi.controllers;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import gov.usgs.owi.nldi.dao.LookupDao;
import gov.usgs.owi.nldi.dao.LookupDaoTest;
import gov.usgs.owi.nldi.dao.StreamingDao;
import gov.usgs.owi.nldi.services.LogService;
import gov.usgs.owi.nldi.services.Navigation;
import gov.usgs.owi.nldi.services.Parameters;
import gov.usgs.owi.nldi.springinit.TestSpringConfig;

public class LookupControllerTest {

	private StreamingDao streamingDao;
	@Mock
	private LookupDao lookupDao;
	@Mock
	private Navigation navigation;
	@Mock
	private Parameters parameters;
	@Mock
	private LogService logService;
	private LookupController controller;
	private MockHttpServletResponse response;
	private MockHttpServletRequest request;

	@SuppressWarnings("unchecked")
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		controller = new LookupController(lookupDao, streamingDao, navigation, parameters, TestSpringConfig.TEST_ROOT_URL, logService);
		response = new MockHttpServletResponse();
		request = new MockHttpServletRequest();

		when(logService.logRequest(any(HttpServletRequest.class))).thenReturn(BigInteger.ONE);
		when(lookupDao.getList(any(String.class), anyMap())).thenReturn(new ArrayList<Map<String, Object>>(), null, LookupDaoTest.getTestList());
	}

	@Test
	public void getDataSourcesTest() throws UnsupportedEncodingException {
		List<Map<String, Object>> out = controller.getDataSources(request, response);
		verify(logService).logRequest(any(HttpServletRequest.class));
		verify(logService).logRequestComplete(any(BigInteger.class), any(int.class));
		assertEquals(HttpStatus.OK.value(), response.getStatus());
		assertEquals("[{source=comid, sourceName=NHDPlus comid, features=http://owi-test.usgs.gov:8080/test-url/comid}]", out.toString());
	}

	@Test
	public void getFeaturestest() throws IOException {
		controller.getFeatures(request, response, null);
		verify(logService).logRequest(any(HttpServletRequest.class));
		verify(logService).logRequestComplete(any(BigInteger.class), any(int.class));
		assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
		assertEquals("This functionality is not implemented.", response.getErrorMessage());
	}

	@Test
	public void getRegisteredFeatureTest() throws IOException {
		controller.getRegisteredFeature(request, response, null, null);
		verify(logService).logRequest(any(HttpServletRequest.class));
		verify(logService).logRequestComplete(any(BigInteger.class), any(int.class));
		//this is a BAD_REQUEST because the BaseController.streamFeatures dependencies are not all mocked
		assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
	}

	@Test
	public void getNavigationTypesTest() throws UnsupportedEncodingException {
		controller.getNavigationTypes(request, response, null, null);
		verify(logService).logRequest(any(HttpServletRequest.class));
		verify(logService).logRequestComplete(any(BigInteger.class), any(int.class));
		assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());

		response = new MockHttpServletResponse();
		controller.getNavigationTypes(request, response, null, null);
		verify(logService, times(2)).logRequest(any(HttpServletRequest.class));
		verify(logService, times(2)).logRequestComplete(any(BigInteger.class), any(int.class));
		assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());

		response = new MockHttpServletResponse();
		Map<String, Object> out = controller.getNavigationTypes(request, response, "test", "test123");
		verify(logService, times(3)).logRequest(any(HttpServletRequest.class));
		verify(logService, times(3)).logRequestComplete(any(BigInteger.class), any(int.class));
		assertEquals(HttpStatus.OK.value(), response.getStatus());
		assertEquals("{upstreamMain=http://owi-test.usgs.gov:8080/test-url/test/test123/navigate/UM, upstreamTributaries=http://owi-test.usgs.gov:8080/test-url/test/test123/navigate/UT, downstreamMain=http://owi-test.usgs.gov:8080/test-url/test/test123/navigate/DM, downstreamDiversions=http://owi-test.usgs.gov:8080/test-url/test/test123/navigate/DD}",
				out.toString());
	}

}
