package gov.usgs.owi.nldi.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.apache.ibatis.session.ResultHandler;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletResponse;

import gov.usgs.owi.nldi.dao.CountDao;
import gov.usgs.owi.nldi.dao.StreamingDao;
import gov.usgs.owi.nldi.services.Navigation;
import gov.usgs.owi.nldi.transform.ITransformer;
public class RestControllerTest {

	@Mock
	private CountDao countDao;
	@Mock
	private StreamingDao streamingDao;
	@Mock
	private Navigation navigation;
	@Mock
	private ITransformer transformer;

	private RestController controller;

    @Before
    public void setup() {
    	MockitoAnnotations.initMocks(this);
    	controller = new RestController(countDao, streamingDao, navigation);
    }

	@Test
    @SuppressWarnings("unchecked")
    public void streamResultsTest() {
    	controller.streamResults(transformer, null, null);
    	verify(streamingDao).stream(anyString(), anyMap(), any(ResultHandler.class));
    	verify(transformer).end();
    }

	@Test
    @SuppressWarnings("unchecked")
    public void addHeadersTest() {
		when(countDao.count(anyString(), anyMap())).thenReturn("912");
		MockHttpServletResponse response = new MockHttpServletResponse();
		controller.addHeaders(response, null, null);
		
		verify(countDao).count(anyString(), anyMap());
		assertTrue(response.containsHeader(RestController.HEADER_CONTENT_TYPE));
		assertEquals(RestController.MIME_TYPE_GEOJSON, response.getHeader(RestController.HEADER_CONTENT_TYPE));
		assertTrue(response.containsHeader("null" + RestController.COUNT_SUFFIX));
		assertEquals("912", response.getHeader("null" + RestController.COUNT_SUFFIX));
	}

}
