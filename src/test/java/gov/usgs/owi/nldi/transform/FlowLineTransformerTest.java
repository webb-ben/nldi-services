package gov.usgs.owi.nldi.transform;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;

public class FlowLineTransformerTest {

	protected FlowLineTransformer transformer;
	protected MockHttpServletResponse response;

	@Before
	public void beforeTest() throws IOException {
		response = new MockHttpServletResponse();
		transformer = new FlowLineTransformer(response);
	}

	@After
	public void afterTest() throws Exception {
		transformer.close();
	}

	@Test
	public void writePropertiesTest() {
		Map<String, Object> map = new HashMap<>();
		map.put(FlowLineTransformer.NHDPLUS_COMID, "13293474");
		map.put(FeatureTransformer.COMID, "47439231");
		map.put(FeatureTransformer.IDENTIFIER, "identifierValue");
		map.put(FeatureTransformer.NAME, "nameValue");
		map.put(FeatureTransformer.URI, "uriValue");
		try {
			transformer.g.writeStartObject();
			transformer.writeProperties(transformer.g, map);
			transformer.g.writeEndObject();
			//need to flush the JsonGenerator to get at output. 
			transformer.g.flush();
			assertEquals("{\"nhdplus_comid\":\"13293474\"}", response.getContentAsString());
		} catch (IOException e) {
			fail(e.getLocalizedMessage());
		}
	}

}
