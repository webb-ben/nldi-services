package gov.usgs.owi.nldi.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.OutputStream;
import java.util.HashMap;

import org.apache.ibatis.session.ResultHandler;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;

import gov.usgs.owi.nldi.NavigationMode;
import gov.usgs.owi.nldi.dao.CountDao;
import gov.usgs.owi.nldi.dao.LookupDao;
import gov.usgs.owi.nldi.dao.StreamingDao;
import gov.usgs.owi.nldi.services.Navigation;
import gov.usgs.owi.nldi.services.Parameters;
import gov.usgs.owi.nldi.springinit.TestSpringConfig;
import gov.usgs.owi.nldi.transform.ITransformer;

public class BaseControllerTest {

	@Mock
	private CountDao countDao;
	@Mock
	private LookupDao lookupDao;
	@Mock
	private StreamingDao streamingDao;
	@Mock
	private Navigation navigation;
	@Mock
	private Parameters parameters;
	@Mock
	private ITransformer transformer;
	private MockHttpServletResponse response;

	private class TestBaseController extends BaseController {
		public TestBaseController(CountDao inCountDao, LookupDao inLookupDao, StreamingDao inStreamingDao, Navigation inNavigation, Parameters inParameters) {
			super(inCountDao, inLookupDao, inStreamingDao, inNavigation, inParameters, TestSpringConfig.TEST_ROOT_URL);
		}
	}
	
	private TestBaseController controller;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		controller = new TestBaseController(countDao, lookupDao, streamingDao, navigation, parameters);
		response = new MockHttpServletResponse();
	}

	@Test
	public void addContentHeaderTest() {
		controller.addContentHeader(response);

		assertTrue(response.containsHeader(NetworkController.HEADER_CONTENT_TYPE));
		assertEquals(NetworkController.MIME_TYPE_GEOJSON, response.getHeader(NetworkController.HEADER_CONTENT_TYPE));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void addHeadersTest() {
		when(countDao.count(anyString(), anyMap())).thenReturn("912");
		controller.addHeaders(response, null, null);
		verify(countDao, never()).count(anyString(), anyMap());

		controller.addHeaders(response, "null", null);
		verify(countDao).count(anyString(), anyMap());
		assertTrue(response.containsHeader(NetworkController.HEADER_CONTENT_TYPE));
		assertEquals(NetworkController.MIME_TYPE_GEOJSON, response.getHeader(NetworkController.HEADER_CONTENT_TYPE));
		assertTrue(response.containsHeader("null" + NetworkController.COUNT_SUFFIX));
		assertEquals("912", response.getHeader("null" + NetworkController.COUNT_SUFFIX));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void getSessionIdTest() {
		when(navigation.navigate(any(OutputStream.class), anyMap())).thenReturn("abc");
		assertEquals("abc", controller.getSessionId(null, new HashMap<String, Object>()));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void streamFeaturesTest() {
		when(countDao.count(anyString(), anyMap())).thenReturn("912");
		when(navigation.navigate(any(OutputStream.class), anyMap())).thenReturn(null, "abc");
		controller.streamFeatures(response, "123", "navigationMode", "456", "789", "dataSource", true);
		verify(countDao, never()).count(anyString(), anyMap());
		verify(streamingDao, never()).stream(anyString(), anyMap(), any(ResultHandler.class));
		assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());

		controller.streamFeatures(response, "123", "navigationMode", "456", "789", "dataSource", true);
		verify(countDao).count(anyString(), anyMap());
		verify(streamingDao).stream(anyString(), anyMap(), any(ResultHandler.class));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void streamFlowLinesTest() {
		when(countDao.count(anyString(), anyMap())).thenReturn("912");
		when(navigation.navigate(any(OutputStream.class), anyMap())).thenReturn(null, "abc");
		controller.streamFlowLines(response, "123", "navigationMode", "456", "789", true);
		verify(countDao, never()).count(anyString(), anyMap());
		verify(streamingDao, never()).stream(anyString(), anyMap(), any(ResultHandler.class));
		assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());

		controller.streamFlowLines(response, "123", "navigationMode", "456", "789", true);
		verify(countDao).count(anyString(), anyMap());
		verify(streamingDao).stream(anyString(), anyMap(), any(ResultHandler.class));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void streamResultsTest() {
		controller.streamResults(transformer, null, null);
		verify(streamingDao).stream(anyString(), anyMap(), any(ResultHandler.class));
		verify(transformer).end();
	}

	@Test
	public void isLegacyTest() {
		assertTrue(controller.isLegacy("true", NavigationMode.DM.toString()));
		assertTrue(controller.isLegacy("true", null));
		assertTrue(controller.isLegacy(" true ", null));
		assertTrue(controller.isLegacy("True ", null));
		
		assertFalse(controller.isLegacy(null, null));
		assertFalse(controller.isLegacy("", null));
		assertFalse(controller.isLegacy("  ", null));
		assertFalse(controller.isLegacy("false", null));
		assertFalse(controller.isLegacy("abc", null));

		assertTrue(controller.isLegacy(null, NavigationMode.DD.toString()));
		assertTrue(controller.isLegacy(null, NavigationMode.PP.toString()));
	}
}
