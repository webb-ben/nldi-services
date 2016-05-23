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

import gov.usgs.owi.nldi.springinit.TestSpringConfig;

public class FlowLineTransformerTest {

	protected FlowLineTransformer transformer;
	protected ByteArrayOutputStream baos;

	@Before
	public void initTest() {
		baos = new ByteArrayOutputStream();
		transformer = new FlowLineTransformer(baos, TestSpringConfig.TEST_ROOT_URL);
		transformer.init();
	}

	@After
	public void closeTest() throws IOException {
		transformer.close();
	}

	@Test
	public void writePropertiesTest() {
		Map<String, Object> map = new HashMap<>();
		map.put(MapToGeoJsonTransformer.SHAPE, "{\"type\":\"LineString\",\"coordinates\":[[-89.2572407051921, 43.2039759978652],[-89.2587703019381, 43.204960398376]]}");
		map.put(FlowLineTransformer.NHDPLUS_COMID, "13293474");
		map.put(FeatureTransformer.COMID, "47439231");
		map.put(FeatureTransformer.IDENTIFIER, "identifierValue");
		map.put(FeatureTransformer.NAME, "nameValue");
		map.put(FeatureTransformer.URI, "uriValue");
		try {
			transformer.g.writeStartObject();
			transformer.writeProperties(map);
			transformer.g.writeEndObject();
			//need to flush the JsonGenerator to get at output. 
			transformer.g.flush();
			assertEquals(68, baos.size());
			assertEquals(MapToGeoJsonTransformerTest.HEADER_TEXT + "{\"nhdplus_comid\":\"13293474\"}",
					new String(baos.toByteArray(), MapToGeoJsonTransformer.DEFAULT_ENCODING));
		} catch (IOException e) {
			fail(e.getLocalizedMessage());
		}

		map.put(MapToGeoJsonTransformer.SHAPE, "{\"type\":\"LineString\",\"coordinates\":[[-89.2489906027913, 43.2102229967713],[-89.2497089058161, 43.2099935933948]]}");
		map.put(FlowLineTransformer.NHDPLUS_COMID, "13294118");
		map.put(FeatureTransformer.COMID, "81149213");
		map.put(FeatureTransformer.IDENTIFIER, "identifier2Value");
		map.put(FeatureTransformer.NAME, "name2Value");
		map.put(FeatureTransformer.URI, "uri2Value");

		try {
			transformer.g.writeStartObject();
			transformer.writeProperties(map);
			transformer.g.writeEndObject();
			//need to flush the JsonGenerator to get at output. 
			transformer.g.flush();
			assertEquals(97, baos.size());
			assertEquals(MapToGeoJsonTransformerTest.HEADER_TEXT + "{\"nhdplus_comid\":\"13293474\"}"
					+ ",{\"nhdplus_comid\":\"13294118\"}",
					new String(baos.toByteArray(), MapToGeoJsonTransformer.DEFAULT_ENCODING));
		} catch (IOException e) {
			fail(e.getLocalizedMessage());
		}

	}

}
