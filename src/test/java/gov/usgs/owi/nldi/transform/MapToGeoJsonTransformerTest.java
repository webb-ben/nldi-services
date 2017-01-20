package gov.usgs.owi.nldi.transform;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
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

	protected TTransformer transformer;
	protected MockHttpServletResponse response;

	private class TTransformer extends MapToGeoJsonTransformer {
		public int writeDataCalled = 0;
		public int writePropertiesCalled = 0;
		public TTransformer(HttpServletResponse response) throws IOException {
			super(response, TestSpringConfig.TEST_ROOT_URL, TEST_COUNT_HEADER);
		}
		@Override
		protected void writeData(Map<String, Object> resultMap) {
			writeDataCalled = writeDataCalled + 1;
			super.writeData(resultMap);
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
		transformer = new TTransformer(response);
	}

	@After
	public void afterTest() throws Exception {
		transformer.close();
	}

	@Test
	public void writeTest() {
		//Don't process null results
		transformer.write((Object) null);
		assertEquals(0, transformer.writeDataCalled);
		assertEquals(0, transformer.writePropertiesCalled);

		//Don't process results that aren't a map
		transformer.write((Object) "ABCDEFG");
		assertEquals(0, transformer.writeDataCalled);
		assertEquals(0, transformer.writePropertiesCalled);

		Map<String, Object> result = new HashMap<>();
		result.put("A", "1");
		result.put("B", "2");
		result.put(MapToGeoJsonTransformer.TOTAL_ROWS, 569);

		transformer.write((Object) result);
		transformer.write((Object) result);
		assertEquals(2, transformer.writeDataCalled);
		assertEquals(2, transformer.writePropertiesCalled);

		try {
			//need to flush the JsonGenerator to get at output. 
			transformer.g.flush();
			assertEquals(HEADER_TEXT + "{\"type\":\"Feature\",\"geometry\":{},\"properties\":{\"prop\":\"propValue\"}},{\"type\":\"Feature\",\"geometry\":{},\"properties\":{\"prop\":\"propValue\"}}",
					response.getContentAsString());
			assertTrue(response.containsHeader(TEST_COUNT_HEADER));
			assertEquals("569", response.getHeaderValue(TEST_COUNT_HEADER));
		} catch (IOException e) {
			fail(e.getLocalizedMessage());
		}
	}

	@Test
	public void initTest() {
		try {
			Map<String, Object> result = new HashMap<>();
			result.put(MapToGeoJsonTransformer.TOTAL_ROWS, 569);
			transformer.init(response, TestSpringConfig.TEST_ROOT_URL, result);
			//need to flush the JsonGenerator to get at output. 
			transformer.g.flush();
			assertEquals(HEADER_TEXT, response.getContentAsString());
		} catch (IOException e) {
			fail(e.getLocalizedMessage());
		}
	}

	@Test
	public void writeDataTest() {
		transformer.init(response, TestSpringConfig.TEST_ROOT_URL, new HashMap<>());
		Map<String, Object> map = new HashMap<>();
		map.put(MapToGeoJsonTransformer.SHAPE, "{\"type\":\"LineString\",\"coordinates\":[[-89.2572407051921, 43.2039759978652],[-89.2587703019381, 43.204960398376]]}");
		try {
			transformer.writeData(map);
			//need to flush the JsonGenerator to get at output. 
			transformer.g.flush();
			assertEquals(HEADER_TEXT + "{\"type\":\"Feature\",\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[-89.2572407051921, 43.2039759978652],[-89.2587703019381, 43.204960398376]]},\"properties\":{\"prop\":\"propValue\"}}",
					response.getContentAsString());
		} catch (IOException e) {
			fail(e.getLocalizedMessage());
		}

		map.put(MapToGeoJsonTransformer.SHAPE, "{\"type\":\"LineString\",\"coordinates\":[[-89.2489906027913, 43.2102229967713],[-89.2497089058161, 43.2099935933948]]}");

		try {
			transformer.writeData(map);
			//need to flush the JsonGenerator to get at output. 
			transformer.g.flush();
			assertEquals(HEADER_TEXT + "{\"type\":\"Feature\",\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[-89.2572407051921, 43.2039759978652],[-89.2587703019381, 43.204960398376]]},\"properties\":{\"prop\":\"propValue\"}}"
					+ ",{\"type\":\"Feature\",\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[-89.2489906027913, 43.2102229967713],[-89.2497089058161, 43.2099935933948]]},\"properties\":{\"prop\":\"propValue\"}}",
					response.getContentAsString());
		} catch (IOException e) {
			fail(e.getLocalizedMessage());
		}

	}

	@Test
	public void endTestData() {
		try {
			transformer.init(response, TestSpringConfig.TEST_ROOT_URL, new HashMap<>());
			transformer.g.writeStartObject();
			transformer.g.writeFieldName("abc");
			transformer.g.writeStartArray();

			transformer.end();
			assertEquals(HEADER_TEXT + "{\"abc\":[]}]}", response.getContentAsString());
		} catch (IOException e) {
			fail(e.getLocalizedMessage());
		}
	}

	@Test
	public void endTestNoData() {
		transformer.init(response, TestSpringConfig.TEST_ROOT_URL, new HashMap<>());
		try {
			transformer.end();
			assertEquals(MapToGeoJsonTransformerTest.HEADER_TEXT + "]}",
					response.getContentAsString());
		} catch (IOException e) {
			fail(e.getLocalizedMessage());
		}
	}

	@Test
	public void getValueTest() {
		Map<String, Object> map = new HashMap<>();
		map.put("NotNull", "abc/");
		map.put("Null", null);
		assertEquals("abc/", transformer.getValue(map, "NotNull"));
		assertEquals("", transformer.getValue(map, "Null"));
		assertEquals("", transformer.getValue(map, "NoWay"));
	}

}
