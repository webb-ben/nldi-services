package gov.usgs.owi.nldi.transform;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MapToJsonTransformerTest {

	public static final String HEADER_TEXT = "{\"type\":\"FeatureCollection\",\"features\":[";
	
	protected TTransformer transformer;
	protected ByteArrayOutputStream baos;
	
	private class TTransformer extends MapToJsonTransformer {
		public int writeDataCalled = 0;
		public int writePropertiesCalled = 0;
		public TTransformer(OutputStream target) {
			super(target);
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
    public void initTest() {
		baos = new ByteArrayOutputStream();
        transformer = new TTransformer(baos);
    }
    
    @After
    public void closeTest() throws IOException {
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

		transformer.write((Object) result);
		transformer.write((Object) result);
		assertEquals(2, transformer.writeDataCalled);
		assertEquals(2, transformer.writePropertiesCalled);

		try {
			//need to flush the JsonGenerator to get at output. 
			transformer.g.flush();
			assertEquals(173, baos.size());
			assertEquals(HEADER_TEXT + "{\"type\":\"Feature\",\"geometry\":{},\"properties\":{\"prop\":\"propValue\"}},{\"type\":\"Feature\",\"geometry\":{},\"properties\":{\"prop\":\"propValue\"}}",
					new String(baos.toByteArray(), MapToJsonTransformer.DEFAULT_ENCODING));
		} catch (IOException e) {
			fail(e.getLocalizedMessage());
		}
	}

	@Test
	public void initializedTest() {
		try {
			//need to flush the JsonGenerator to get at output. 
			transformer.g.flush();
			assertEquals(40, baos.size());
			assertEquals(HEADER_TEXT,
					new String(baos.toByteArray(), MapToJsonTransformer.DEFAULT_ENCODING));
		} catch (IOException e) {
			fail(e.getLocalizedMessage());
		}
	}

	@Test
	public void writeDataTest() {
		Map<String, Object> map = new HashMap<>();
		map.put("shape", "{\"type\":\"LineString\",\"coordinates\":[[-89.2572407051921, 43.2039759978652],[-89.2587703019381, 43.204960398376]]}");
		try {
			transformer.writeData(map);
			//need to flush the JsonGenerator to get at output. 
			transformer.g.flush();
			assertEquals(216, baos.size());
			assertEquals(HEADER_TEXT + "{\"type\":\"Feature\",\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[-89.2572407051921, 43.2039759978652],[-89.2587703019381, 43.204960398376]]},\"properties\":{\"prop\":\"propValue\"}}",
					new String(baos.toByteArray(), MapToJsonTransformer.DEFAULT_ENCODING));
		} catch (IOException e) {
			fail(e.getLocalizedMessage());
		}

		map.put("shape", "{\"type\":\"LineString\",\"coordinates\":[[-89.2489906027913, 43.2102229967713],[-89.2497089058161, 43.2099935933948]]}");

		try {
			transformer.writeData(map);
			//need to flush the JsonGenerator to get at output. 
			transformer.g.flush();
			assertEquals(394, baos.size());
			assertEquals(HEADER_TEXT + "{\"type\":\"Feature\",\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[-89.2572407051921, 43.2039759978652],[-89.2587703019381, 43.204960398376]]},\"properties\":{\"prop\":\"propValue\"}}"
					+ ",{\"type\":\"Feature\",\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[-89.2489906027913, 43.2102229967713],[-89.2497089058161, 43.2099935933948]]},\"properties\":{\"prop\":\"propValue\"}}",
					new String(baos.toByteArray(), MapToJsonTransformer.DEFAULT_ENCODING));
		} catch (IOException e) {
			fail(e.getLocalizedMessage());
		}

	}
	
	@Test
	public void endTestData() {
		try {
			transformer.g.writeStartObject();
			transformer.g.writeFieldName("abc");
			transformer.g.writeStartArray();
			
			transformer.end();
			assertEquals(52, baos.size());
			assertEquals(HEADER_TEXT + "{\"abc\":[]}]}",
					new String(baos.toByteArray(), MapToJsonTransformer.DEFAULT_ENCODING));
		} catch (IOException e) {
			fail(e.getLocalizedMessage());
		}
	}

	@Test
	public void endTestNoData() {
		try {
			transformer.end();
			assertEquals(42, baos.size());
			assertEquals(MapToJsonTransformerTest.HEADER_TEXT + "]}",
					new String(baos.toByteArray(), MapToJsonTransformer.DEFAULT_ENCODING));
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
