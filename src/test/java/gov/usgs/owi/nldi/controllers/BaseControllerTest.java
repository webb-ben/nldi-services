package gov.usgs.owi.nldi.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.OutputStream;

import org.apache.ibatis.session.ResultHandler;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;

import gov.usgs.owi.nldi.dao.CountDao;
import gov.usgs.owi.nldi.dao.StreamingDao;
import gov.usgs.owi.nldi.services.Navigation;
import gov.usgs.owi.nldi.transform.ITransformer;

public class BaseControllerTest {

	@Mock
	private CountDao countDao;
	@Mock
	private StreamingDao streamingDao;
	@Mock
	private Navigation navigation;
	@Mock
	private ITransformer transformer;
	private MockHttpServletResponse response;

	private class TestBaseController extends BaseController {
		public TestBaseController(CountDao inCountDao, StreamingDao inStreamingDao, Navigation inNavigation) {
			super(inCountDao, inStreamingDao, inNavigation);
		}
	}
	
	private TestBaseController controller;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		controller = new TestBaseController(countDao, streamingDao, navigation);
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

		verify(countDao).count(anyString(), anyMap());
		assertTrue(response.containsHeader(NetworkController.HEADER_CONTENT_TYPE));
		assertEquals(NetworkController.MIME_TYPE_GEOJSON, response.getHeader(NetworkController.HEADER_CONTENT_TYPE));
		assertTrue(response.containsHeader("null" + NetworkController.COUNT_SUFFIX));
		assertEquals("912", response.getHeader("null" + NetworkController.COUNT_SUFFIX));
	}

	@Test
	public void getSessionIdTest() {
		when(navigation.navigate(any(OutputStream.class), anyString(), anyString(), anyString(), anyString())).thenReturn("abc");
		assertEquals("abc", controller.getSessionId(null, null, null, null, null));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void streamFeaturesTest() {
		when(countDao.count(anyString(), anyMap())).thenReturn("912");
		when(navigation.navigate(any(OutputStream.class), anyString(), anyString(), anyString(), anyString())).thenReturn(null, "abc");
		controller.streamFeatures(response, "comid", "navigationMode", "stopComid", "distance", "dataSource");
		verify(countDao, never()).count(anyString(), anyMap());
		verify(streamingDao, never()).stream(anyString(), anyMap(), any(ResultHandler.class));
		assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());

		controller.streamFeatures(response, "comid", "navigationMode", "stopComid", "distance", "dataSource");
		verify(countDao).count(anyString(), anyMap());
		verify(streamingDao).stream(anyString(), anyMap(), any(ResultHandler.class));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void streamFlowLinesTest() {
		when(countDao.count(anyString(), anyMap())).thenReturn("912");
		when(navigation.navigate(any(OutputStream.class), anyString(), anyString(), anyString(), anyString())).thenReturn(null, "abc");
		controller.streamFlowLines(response, "comid", "navigationMode", "stopComid", "distance");
		verify(countDao, never()).count(anyString(), anyMap());
		verify(streamingDao, never()).stream(anyString(), anyMap(), any(ResultHandler.class));
		assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());

		controller.streamFlowLines(response, "comid", "navigationMode", "stopComid", "distance");
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

}
