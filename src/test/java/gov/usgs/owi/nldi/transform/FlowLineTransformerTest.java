package gov.usgs.owi.nldi.transform;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class FlowLineTransformerTest {

	protected FlowLineTransformer transformer;
	protected ByteArrayOutputStream baos;

	@Before
	public void initTest() {
		baos = new ByteArrayOutputStream();
		transformer = new FlowLineTransformer(baos);
		transformer.init();
	}

	@After
	public void closeTest() throws IOException {
		transformer.close();
	}

	@Test
	public void writePropertiesTest() {
		Map<String, Object> map = new HashMap<>();
		map.put("shape", "{\"type\":\"LineString\",\"coordinates\":[[-89.2572407051921, 43.2039759978652],[-89.2587703019381, 43.204960398376]]}");
		map.put("nhdplus_comid", "13293474");
		map.put("comid", "47439231");
		map.put("identifier", "identifierValue");
		map.put("name", "nameValue");
		map.put("uri", "uriValue");
		try {
			transformer.g.writeStartObject();
			transformer.writeProperties(map);
			transformer.g.writeEndObject();
			//need to flush the JsonGenerator to get at output. 
			transformer.g.flush();
			assertEquals(68, baos.size());
			assertEquals(MapToJsonTransformerTest.HEADER_TEXT + "{\"nhdplus_comid\":\"13293474\"}",
					new String(baos.toByteArray(), MapToJsonTransformer.DEFAULT_ENCODING));
		} catch (IOException e) {
			fail(e.getLocalizedMessage());
		}

		map.put("shape", "{\"type\":\"LineString\",\"coordinates\":[[-89.2489906027913, 43.2102229967713],[-89.2497089058161, 43.2099935933948]]}");
		map.put("nhdplus_comid", "13294118");
		map.put("nhdplus_comid", "13294118");
		map.put("comid", "81149213");
		map.put("identifier", "identifier2Value");
		map.put("name", "name2Value");
		map.put("uri", "uri2Value");

		try {
			transformer.g.writeStartObject();
			transformer.writeProperties(map);
			transformer.g.writeEndObject();
			//need to flush the JsonGenerator to get at output. 
			transformer.g.flush();
			assertEquals(97, baos.size());
			assertEquals(MapToJsonTransformerTest.HEADER_TEXT + "{\"nhdplus_comid\":\"13293474\"}"
					+ ",{\"nhdplus_comid\":\"13294118\"}",
					new String(baos.toByteArray(), MapToJsonTransformer.DEFAULT_ENCODING));
		} catch (IOException e) {
			fail(e.getLocalizedMessage());
		}

	}

}
