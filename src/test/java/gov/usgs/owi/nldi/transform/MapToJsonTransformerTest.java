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

import com.fasterxml.jackson.core.JsonGenerator;

public class MapToJsonTransformerTest {

	public static final String INITIAL_JSON = "{\"baz\":[";
	protected static final String TEST_HEADER_NAME = "thName";
	protected static final String TEST_HEADER_VALUE = "thValue";

	protected TestTransformer transformer;
	protected MockHttpServletResponse response;

	private class TestTransformer extends MapToJsonTransformer {
		public int addResponseHeadersCalledCount = 0;
		public int initJsonCalledCount = 0;
		public int writeMapCalledCount = 0;
		
		public TestTransformer(HttpServletResponse response) throws IOException {
			super(response);
		}
		
		@Override
		void addResponseHeaders(HttpServletResponse response, Map<String, Object> resultMap) {
			addResponseHeadersCalledCount = addResponseHeadersCalledCount + 1;
			response.addHeader(TEST_HEADER_NAME, TEST_HEADER_VALUE);
		}
		
		@Override
		void initJson(JsonGenerator jsonGenerator, Map<String, Object> resultMap) {
			initJsonCalledCount = initJsonCalledCount + 1;
			try {
				jsonGenerator.writeStartObject();
				jsonGenerator.writeFieldName("baz"); 
				jsonGenerator.writeStartArray();
			} catch (IOException e) {
				fail(e.getLocalizedMessage());
			}
		}
		
		@Override
		void writeMap(JsonGenerator jsonGenerator, Map<String, Object> resultMap) {
			writeMapCalledCount = writeMapCalledCount + 1;
			try {
				g.writeStartObject();
				jsonGenerator.writeStringField("prop" + writeMapCalledCount, "propValue" + writeMapCalledCount);
				g.writeEndObject();
			} catch (IOException e) {
				fail(e.getLocalizedMessage());
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
	public void writeTest() {
		//Don't process null results
		transformer.write((Object) null);
		assertMethodCallCounts(0, 0, 0);

		//Don't process results that aren't a map
		transformer.write((Object) "ABCDEFG");
		assertMethodCallCounts(0, 0, 0);

		Map<String, Object> result = new HashMap<>();
		result.put("A", "1");
		result.put("B", "2");

		transformer.write((Object) result);
		assertMethodCallCounts(1, 1, 1);

		// headers should be added after first call
		assertTrue(response.containsHeader(TEST_HEADER_NAME));
		assertEquals(TEST_HEADER_VALUE, response.getHeaderValue(TEST_HEADER_NAME));

		// initial json should be set after first call, along with first property
		try {
			transformer.g.flush();
			assertEquals(INITIAL_JSON + "{\"prop1\":\"propValue1\"}", response.getContentAsString());
		} catch (IOException e) {
			fail(e.getLocalizedMessage());
		}

		transformer.write((Object) result);
		assertMethodCallCounts(1, 1, 2);
		
		try {
			transformer.g.flush();
			assertEquals(INITIAL_JSON + "{\"prop1\":\"propValue1\"},{\"prop2\":\"propValue2\"}", response.getContentAsString());
		} catch (IOException e) {
			fail(e.getLocalizedMessage());
		}
		
		transformer.end();
		
		try {
			assertEquals(INITIAL_JSON + "{\"prop1\":\"propValue1\"},{\"prop2\":\"propValue2\"}]}", response.getContentAsString());
		} catch (IOException e) {
			fail(e.getLocalizedMessage());
		}
	}

	private void assertMethodCallCounts(int addResponseCount, int initJsonCount, int writeMapCount) {
		assertEquals(addResponseCount, transformer.addResponseHeadersCalledCount);
		assertEquals(initJsonCount, transformer.initJsonCalledCount);
		assertEquals(writeMapCount, transformer.writeMapCalledCount);
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
