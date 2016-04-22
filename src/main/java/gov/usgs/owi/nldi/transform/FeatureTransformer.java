package gov.usgs.owi.nldi.transform;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public class FeatureTransformer extends MapToJsonTransformer {
	public FeatureTransformer(OutputStream target) {
		super(target);
	}

	@Override
	protected void writeProperties(Map<String, Object> resultMap) {
		try {
			g.writeStringField("comid", getValue(resultMap, "comid"));
			g.writeStringField("identifier", getValue(resultMap, "identifier"));
			g.writeStringField("name", getValue(resultMap, "name"));
			g.writeStringField("uri", getValue(resultMap, "uri"));
		} catch (IOException e) {
			throw new RuntimeException("Error writing json for Feature Properties", e);
		}
	}

}
