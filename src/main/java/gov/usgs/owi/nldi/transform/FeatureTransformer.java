package gov.usgs.owi.nldi.transform;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public class FeatureTransformer extends MapToJsonTransformer {
	public FeatureTransformer(OutputStream target) {
		super(target);
	}

	@Override
	protected void writeProperties(Map<String, Object> resultMap) {
		try {
			g.writeStringField("source", getValue(resultMap, "source_name"));
			g.writeStringField("identifier", getValue(resultMap, "identifier"));
			g.writeStringField("name", getValue(resultMap, "name"));
			g.writeStringField("uri", getValue(resultMap, "uri"));
			g.writeStringField("comid", getValue(resultMap, "comid"));
			if (StringUtils.isNotEmpty(getValue(resultMap, "reachcode"))) {
				g.writeStringField("reachcode", getValue(resultMap, "reachcode"));
			}
			if (StringUtils.isNotEmpty(getValue(resultMap, "measure"))) {
				g.writeStringField("measure", getValue(resultMap, "measure"));
			}
		} catch (IOException e) {
			throw new RuntimeException("Error writing json for Feature Properties", e);
		}
	}

}
