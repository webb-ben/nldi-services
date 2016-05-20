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

public class FeatureTransformerTest {

	protected FeatureTransformer transformer;
	protected ByteArrayOutputStream baos;
	
	@Before
	public void initTest() {
		baos = new ByteArrayOutputStream();
		transformer = new FeatureTransformer(baos);
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
		map.put("source_name", "sourceValue");
		map.put("reachcode", "05020002004263");
		map.put("measure", 1.3823300000);
		try {
			transformer.g.writeStartObject();
			transformer.writeProperties(map);
			transformer.g.writeEndObject();
			//need to flush the JsonGenerator to get at output. 
			transformer.g.flush();
			assertEquals(199, baos.size());
			assertEquals(MapToJsonTransformerTest.HEADER_TEXT + "{\"source\":\"sourceValue\",\"identifier\":\"identifierValue\",\"name\":\"nameValue\","
					+ "\"uri\":\"uriValue\",\"comid\":\"47439231\",\"reachcode\":\"05020002004263\",\"measure\":\"1.38233\"}",
					new String(baos.toByteArray(), MapToJsonTransformer.DEFAULT_ENCODING));
		} catch (IOException e) {
			fail(e.getLocalizedMessage());
		}

		map.clear();
		map.put("shape", "{\"type\":\"LineString\",\"coordinates\":[[-89.2489906027913, 43.2102229967713],[-89.2497089058161, 43.2099935933948]]}");
		map.put("nhdplus_comid", "13294118");
		map.put("comid", "81149213");
		map.put("identifier", "identifier2Value");
		map.put("name", "name2Value");
		map.put("uri", "uri2Value");
		map.put("source_name", "source2Value");

		try {
			transformer.g.writeStartObject();
			transformer.writeProperties(map);
			transformer.g.writeEndObject();
			//need to flush the JsonGenerator to get at output. 
			transformer.g.flush();
			assertEquals(314, baos.size());
			assertEquals(MapToJsonTransformerTest.HEADER_TEXT
					+ "{\"source\":\"sourceValue\",\"identifier\":\"identifierValue\",\"name\":\"nameValue\","
						+ "\"uri\":\"uriValue\",\"comid\":\"47439231\",\"reachcode\":\"05020002004263\",\"measure\":\"1.38233\"}"
					+ ",{\"source\":\"source2Value\",\"identifier\":\"identifier2Value\",\"name\":\"name2Value\","
						+ "\"uri\":\"uri2Value\",\"comid\":\"81149213\"}",
					new String(baos.toByteArray(), MapToJsonTransformer.DEFAULT_ENCODING));
		} catch (IOException e) {
			fail(e.getLocalizedMessage());
		}

	}

}
