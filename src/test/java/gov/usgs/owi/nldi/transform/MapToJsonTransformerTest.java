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
import org.mockito.MockitoAnnotations;



public class MapToJsonTransformerTest {

	protected MapToJsonTransformer transformer;
	protected ByteArrayOutputStream baos;
	
	private class TTransformer extends MapToJsonTransformer {
		public int writeHeaderCalled = 0;
		public int writeDataCalled = 0;
		public TTransformer(OutputStream target) {
			super(target);
		}
		@Override
		protected void writeHeader() {
			writeHeaderCalled = writeHeaderCalled + 1;
		}
		@Override
		protected void writeData(Map<String, Object> resultMap) {
			writeDataCalled = writeDataCalled + 1;
		}
	}
	

    @Before
    public void initTest() {
        MockitoAnnotations.initMocks(this);
		baos = new ByteArrayOutputStream();
        transformer = new MapToJsonTransformer(baos);
    }
    
    @After
    public void closeTest() throws IOException {
    	transformer.close();
    }

	@Test
	public void writeTest() {
		TTransformer transformer = new TTransformer(new ByteArrayOutputStream());

		//Don't process null results
		transformer.write((Object) null);
		assertEquals(0, transformer.writeHeaderCalled);
		assertEquals(0, transformer.writeDataCalled);
		transformer.end();

		//Don't process results that aren't a map
		transformer.write((Object) "ABCDEFG");
		assertEquals(0, transformer.writeHeaderCalled);
		assertEquals(0, transformer.writeDataCalled);
		transformer.end();

		Map<String, Object> result = new HashMap<>();
		result.put("A", "1");
		result.put("B", "2");

		transformer.write((Object) result);
		transformer.write((Object) result);
		assertEquals(1, transformer.writeHeaderCalled);
		assertEquals(2, transformer.writeDataCalled);
		transformer.end();
		
		try {
			transformer.close();
		} catch (IOException e) {
			fail(e.getLocalizedMessage());
		}
	}

	@Test
	public void writeHeaderTest() {
		try {
			transformer.writeHeader();
			assertEquals(40, baos.size());
			assertEquals("{\"type\":\"FeatureCollection\",\"features\":[",
					new String(baos.toByteArray(), MapToJsonTransformer.DEFAULT_ENCODING));
		} catch (IOException e) {
			fail(e.getLocalizedMessage());
		}
	}

	@Test
	public void writeDataTest() {
		Map<String, Object> map = new HashMap<>();
		map.put("shape", "{\"type\":\"LineString\",\"coordinates\":[[-89.2572407051921, 43.2039759978652],[-89.2587703019381, 43.204960398376]]}");
		map.put("nhdplus_comid", "13293474");
		try {
			transformer.writeData(map);
			assertEquals(184, baos.size());
			assertEquals("{\"type\":\"Feature\",\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[-89.2572407051921, 43.2039759978652],[-89.2587703019381, 43.204960398376]]},\"properties\":{\"nhdplus_comid\":\"13293474\"}}",
					new String(baos.toByteArray(), MapToJsonTransformer.DEFAULT_ENCODING));
		} catch (IOException e) {
			fail(e.getLocalizedMessage());
		}

		map.put("shape", "{\"type\":\"LineString\",\"coordinates\":[[-89.2489906027913, 43.2102229967713],[-89.2497089058161, 43.2099935933948]]}");
		map.put("nhdplus_comid", "13294118");

		try {
			transformer.writeData(map);
			assertEquals(370, baos.size());
			assertEquals("{\"type\":\"Feature\",\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[-89.2572407051921, 43.2039759978652],[-89.2587703019381, 43.204960398376]]},\"properties\":{\"nhdplus_comid\":\"13293474\"}}"
					+ ",{\"type\":\"Feature\",\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[-89.2489906027913, 43.2102229967713],[-89.2497089058161, 43.2099935933948]]},\"properties\":{\"nhdplus_comid\":\"13294118\"}}",
					new String(baos.toByteArray(), MapToJsonTransformer.DEFAULT_ENCODING));
		} catch (IOException e) {
			fail(e.getLocalizedMessage());
		}

	}
	
	@Test
	public void endTest() {
		try {
			transformer.end();
			assertEquals(0, baos.size());
			
			transformer.first = false;
			transformer.end();
			assertEquals("]}",
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

	@Test
	public void getValueEncodeTest() {
		Map<String, Object> map = new HashMap<>();
		map.put("NotNull", "abc/");
		map.put("Null", null);
		assertEquals("abc\\/", transformer.getValueEncode(map, "NotNull"));
		assertEquals("", transformer.getValueEncode(map, "Null"));
		assertEquals("", transformer.getValueEncode(map, "NoWay"));
	}

	@Test
	public void encodeTest() {
		assertEquals("abc", transformer.encode("abc"));
		assertEquals("<d\\/ae>", transformer.encode("<d/ae>"));
	}

	@Test
	public void writeToStream() {
		try {
			transformer.writeToStream(null);
			assertEquals(0, baos.size());

			transformer.writeToStream("abc");
			assertEquals(3, baos.size());
			assertEquals("abc", new String(baos.toByteArray(), MapToJsonTransformer.DEFAULT_ENCODING));

			transformer.writeToStream("\u00A7\u00AEabcDE\u00A9\u00B1");
			assertEquals(16, baos.size());
			assertEquals("abc\u00A7\u00AEabcDE\u00A9\u00B1", new String(baos.toByteArray(), MapToJsonTransformer.DEFAULT_ENCODING));

			transformer.end();
			transformer.close();
		} catch (IOException e) {
			fail(e.getLocalizedMessage());
		}
	}

}
