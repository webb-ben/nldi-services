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

import java.util.HashMap;

import org.apache.ibatis.session.ResultHandler;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;

import gov.usgs.owi.nldi.NavigationMode;
import gov.usgs.owi.nldi.dao.LookupDao;
import gov.usgs.owi.nldi.dao.StreamingDao;
import gov.usgs.owi.nldi.services.Navigation;
import gov.usgs.owi.nldi.services.Parameters;
import gov.usgs.owi.nldi.springinit.TestSpringConfig;
import gov.usgs.owi.nldi.transform.ITransformer;

public class BaseControllerTest {

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
		public TestBaseController(LookupDao inLookupDao, StreamingDao inStreamingDao, Navigation inNavigation, Parameters inParameters) {
			super(inLookupDao, inStreamingDao, inNavigation, inParameters, TestSpringConfig.TEST_ROOT_URL);
		}
	}
	
	private TestBaseController controller;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		controller = new TestBaseController(lookupDao, streamingDao, navigation, parameters);
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
	public void getSessionIdTest() throws Exception {
		when(navigation.navigate(anyMap())).thenReturn(new HashMap<String, String>());
		when(navigation.interpretResult(anyMap())).thenReturn("abc");
		assertEquals("abc", controller.getSessionId(new HashMap<String, Object>()));
		verify(navigation).navigate(anyMap());
		verify(navigation).interpretResult(anyMap());
	}

	@Test
	@SuppressWarnings("unchecked")
	public void streamFeaturesTest() throws Exception {
		controller.streamFeatures(response, "123", "navigationMode", "456", "789", "dataSource", false);
		verify(streamingDao).stream(anyString(), anyMap(), any(ResultHandler.class));
		verify(navigation, never()).navigate(anyMap());
		verify(navigation, never()).interpretResult(anyMap());
		assertEquals(HttpStatus.OK.value(), response.getStatus());
	}

	@Test
	@SuppressWarnings("unchecked")
	public void streamFeaturesLegacyTest() throws Exception {
		when(navigation.navigate(anyMap())).thenReturn(new HashMap<String, String>());
		when(navigation.interpretResult(anyMap())).thenReturn(null, "abc");

		controller.streamFeatures(response, "123", "navigationMode", "456", "789", "dataSource", true);
		verify(streamingDao, never()).stream(anyString(), anyMap(), any(ResultHandler.class));
		assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());

		controller.streamFeatures(response, "123", "navigationMode", "456", "789", "dataSource", true);
		verify(streamingDao).stream(anyString(), anyMap(), any(ResultHandler.class));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void streamFlowLinesTest() throws Exception {
		controller.streamFlowLines(response, "123", "navigationMode", "456", "789", false);
		verify(streamingDao).stream(anyString(), anyMap(), any(ResultHandler.class));
		verify(navigation, never()).navigate(anyMap());
		verify(navigation, never()).interpretResult(anyMap());
		assertEquals(HttpStatus.OK.value(), response.getStatus());
	}

	@Test
	@SuppressWarnings("unchecked")
	public void streamFlowLinesLegacyTest() throws Exception {
		when(navigation.navigate(anyMap())).thenReturn(new HashMap<String, String>());
		when(navigation.interpretResult(anyMap())).thenReturn(null, "abc");
		controller.streamFlowLines(response, "123", "navigationMode", "456", "789", true);
		verify(streamingDao, never()).stream(anyString(), anyMap(), any(ResultHandler.class));
		assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());

		controller.streamFlowLines(response, "123", "navigationMode", "456", "789", true);
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

		assertFalse(controller.isLegacy(null, NavigationMode.DD.toString()));
		assertFalse(controller.isLegacy(null, NavigationMode.DM.toString()));
		assertFalse(controller.isLegacy(null, NavigationMode.UM.toString()));
		assertTrue(controller.isLegacy(null, NavigationMode.UT.toString()));
		assertTrue(controller.isLegacy(null, NavigationMode.PP.toString()));
	}
}
