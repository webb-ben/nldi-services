package gov.usgs.owi.nldi.transform;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Map;

import org.springframework.util.StringUtils;

import gov.usgs.owi.nldi.dao.NavigationDao;
import gov.usgs.owi.nldi.services.Parameters;

public class FeatureTransformer extends MapToGeoJsonTransformer {

	public static final String COMID = Parameters.COMID;
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
		g.writeStringField(COMID, getValue(resultMap, COMID));
		if (StringUtils.hasText(getValue(resultMap, REACHCODE))) {
			g.writeStringField(REACHCODE, getValue(resultMap, REACHCODE));
		}
		if (StringUtils.hasText(getValue(resultMap, MEASURE))) {
			g.writeStringField(MEASURE, getValue(resultMap, MEASURE));
		}
		g.writeStringField(NAVIGATION, String.join("/", rootUrl, source.toLowerCase(), URLEncoder.encode(identifier, DEFAULT_ENCODING), NavigationDao.NAVIGATE));
	}

}
