package gov.usgs.owi.nldi.services;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.NumberUtils;
import org.springframework.util.StringUtils;

import gov.usgs.owi.nldi.transform.FeatureTransformer;

@Service
public class Parameters {
	private static final Logger LOG = LoggerFactory.getLogger(Navigation.class);

	public static final String FEATURE_SOURCE = "featureSource";
	public static final String FEATURE_ID = "featureID";
	public static final String COMID = FeatureTransformer.COMID;
	public static final String DD = "DD";
	public static final String DISTANCE = "distance";
	public static final String DM = "DM";
	public static final String DOWNSTREAM_DIVERSIONS = "downstreamDiversions";
	public static final String DOWNSTREAM_MAIN = "downstreamMain";
	public static final String NAVIGATION_MODE = "navigationMode";
	public static final String UM = "UM";
	public static final String UT = "UT";
	public static final String UPSTREAM_MAIN = "upstreamMain";
	public static final String UPSTREAM_TRIBUTARIES = "upstreamTributaries";
	public static final String STOP_COMID = "stopComid";
	public static final String LEGACY = "legacy";

	public Map<String, Object> processParameters(final String comid, final String navigationMode,
			final String distance, final String stopComid) {
		Map<String, Object> parameterMap = new HashMap<> ();

		if (StringUtils.hasText(comid)) {
			LOG.debug("comid:" + comid);
			parameterMap.put(COMID, NumberUtils.parseNumber(comid, Integer.class));
		}
		if (StringUtils.hasText(navigationMode)) {
			LOG.debug("navigationMode:" + navigationMode);
			parameterMap.put(NAVIGATION_MODE, navigationMode);
		}
		if (StringUtils.hasText(distance)) {
			LOG.debug("distance:" + distance);
			parameterMap.put(DISTANCE, NumberUtils.parseNumber(distance, BigDecimal.class));
		}
		if (StringUtils.hasText(stopComid)) {
			LOG.debug("stopComid:" + stopComid);
			parameterMap.put(STOP_COMID, NumberUtils.parseNumber(stopComid, Integer.class));
		}

		LOG.debug("Request Parameters:" + parameterMap.toString());

		return parameterMap;
	}

}
