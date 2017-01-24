package gov.usgs.owi.nldi.transform;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;

import gov.usgs.owi.nldi.springinit.TestSpringConfig;

public class MapToGeoJsonTransformerTest {

	public static final String HEADER_TEXT = "{\"type\":\"FeatureCollection\",\"features\":[";
	protected static final String TEST_COUNT_HEADER = "abc";

	protected TestTransformer transformer;
	protected MockHttpServletResponse response;

	private class TestTransformer extends MapToGeoJsonTransformer {
		public int writePropertiesCalled = 0;
		
		public TestTransformer(HttpServletResponse response) throws IOException {
			super(response, TestSpringConfig.TEST_ROOT_URL, TEST_COUNT_HEADER);
		}
		
		@Override
		protected void writeProperties(Map<String, Object> resultMap) {
			writePropertiesCalled = writePropertiesCalled + 1;
			try {
				g.writeStringField("prop", "propValue");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Before
	public void beforeTest() throws IOException {
		response = new MockHttpServletResponse();
		transformer = new TestTransformer(response);
	}

	@After
	public void afterTest() throws Exception {
		transformer.close();
	}

	@Test
	public void writeMapTest() {
		Map<String, Object> map = new HashMap<>();
		map.put("A", "1");
		map.put("B", "2");
		map.put(MapToGeoJsonTransformer.TOTAL_ROWS, 569);
		map.put(MapToGeoJsonTransformer.SHAPE, "{\"type\":\"LineString\",\"coordinates\":[[-89.2572407051921, 43.2039759978652],[-89.2587703019381, 43.204960398376]]}");

		transformer.writeMap(transformer.g, map);
		assertEquals(1, transformer.writePropertiesCalled);

		try {
			//need to flush the JsonGenerator to get at output. 
			transformer.g.flush();
			assertEquals("{\"type\":\"Feature\""
					+ ",\"geometry\":{"
					+ "\"type\":\"LineString\",\"coordinates\":[[-89.2572407051921, 43.2039759978652],[-89.2587703019381, 43.204960398376]]}"
					+ ",\"properties\":{\"prop\":\"propValue\"}}",
					response.getContentAsString());
		} catch (IOException e) {
			fail(e.getLocalizedMessage());
		}
	}
}
