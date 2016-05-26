package gov.usgs.owi.nldi.transform;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public class FlowLineTransformer extends MapToGeoJsonTransformer {

	public static final String NHDPLUS_COMID = "nhdplus_comid";

	public FlowLineTransformer(OutputStream target, String rootUrl) {
		super(target, rootUrl);
	}

	@Override
	protected void writeProperties(Map<String, Object> resultMap) throws IOException {
		g.writeStringField(NHDPLUS_COMID, getValue(resultMap, NHDPLUS_COMID));
	}

}
