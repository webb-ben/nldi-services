package gov.usgs.owi.nldi.transform;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import gov.usgs.owi.nldi.dao.NavigationDao;

public class FeatureTransformer extends MapToGeoJsonTransformer {

	public static final String COMID = "comid";
	public static final String IDENTIFIER = "identifier";
	public static final String MEASURE = "measure";
	public static final String NAME = "name";
	public static final String NAVIGATION = "navigation";
	public static final String REACHCODE = "reachcode";
	public static final String SOURCE = "source";
	public static final String SOURCE_NAME = "sourceName";
	public static final String SOURCE_NAME_DB = "source_name";
	public static final String URI = "uri";
	
	public FeatureTransformer(OutputStream target, String rootUrl) {
		super(target, rootUrl);
	}

	@Override
	protected void writeProperties(Map<String, Object> resultMap) throws IOException {
		String source = getValue(resultMap, SOURCE);
		String identifier = getValue(resultMap, IDENTIFIER);
		g.writeStringField(SOURCE, source);
		g.writeStringField(SOURCE_NAME, getValue(resultMap, SOURCE_NAME_DB));
		g.writeStringField(IDENTIFIER, identifier);
		g.writeStringField(NAME, getValue(resultMap, NAME));
		g.writeStringField(URI, getValue(resultMap, URI));
		g.writeStringField(FeatureTransformer.COMID, getValue(resultMap, FeatureTransformer.COMID));
		if (StringUtils.isNotEmpty(getValue(resultMap, REACHCODE))) {
			g.writeStringField(REACHCODE, getValue(resultMap, REACHCODE));
		}
		if (StringUtils.isNotEmpty(getValue(resultMap, MEASURE))) {
			g.writeStringField(MEASURE, getValue(resultMap, MEASURE));
		}
		g.writeStringField(NAVIGATION, String.join("/", rootUrl, source, identifier, NavigationDao.NAVIGATE));
	}

}
