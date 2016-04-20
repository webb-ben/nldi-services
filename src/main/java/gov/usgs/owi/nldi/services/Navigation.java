package gov.usgs.owi.nldi.services;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.NumberUtils;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;

import gov.usgs.owi.nldi.dao.NavigationDao;

@Service
public class Navigation {
	private static final Logger LOG = LoggerFactory.getLogger(Navigation.class);

	public static final String COMID = "comid";
	public static final String NAVIGATION_MODE = "navigationMode";
	public static final String STOP_COMID = "stopComid";
	public static final String DISTANCE = "distance";

	protected final NavigationDao navigationDao;

	@Autowired
	public Navigation(NavigationDao inNavigationDao) {
		navigationDao = inNavigationDao;
	}

	public String navigate(OutputStream responseStream, final String comid, final String navigationMode,
			final String distance, final String stopComid) {
		LOG.trace("entering navigation");

		Map<String, Object> parameterMap = processParameters(comid, navigationMode, distance, stopComid);
		LOG.trace("parameters processed");

//		String sessionId = navigationDao.generateSessionId();
//		parameterMap.put("sessionId", sessionId);

		Map<?,?> navigationResult = navigationDao.navigate(parameterMap);
		LOG.trace("navigation built");

		return interpretResult(responseStream, navigationResult);
//		interpretResult(responseStream, parameterMap, navigationResult);
//		LOG.trace("leaving navigation");

//		return sessionId;
	}

	protected Map<String, Object> processParameters(final String comid, final String navigationMode,
			final String distance, final String stopComid) {
		Map<String, Object> parameterMap = new HashMap<> ();
	
		if (StringUtils.isNotBlank(comid)) {
			parameterMap.put(COMID, NumberUtils.parseNumber(comid, Integer.class));
		}
		if (StringUtils.isNotBlank(navigationMode)) {
			parameterMap.put(NAVIGATION_MODE, navigationMode);
		}
		if (StringUtils.isNotBlank(distance)) {
			parameterMap.put(DISTANCE, NumberUtils.parseNumber(distance, BigDecimal.class));
		}
		if (StringUtils.isNotBlank(stopComid)) {
			parameterMap.put(STOP_COMID, NumberUtils.parseNumber(stopComid, Integer.class));
		}
					
		LOG.debug("Request Parameters:" + parameterMap.toString());

		return parameterMap;
	}

//	protected String interpretResult(OutputStream responseStream, Map<String, Object> parameterMap, Map<?,?> navigationResult) {
	protected String interpretResult(OutputStream responseStream, Map<?,?> navigationResult) {
		//An Error Result - {navigate=(,,,,-1,"Valid navigation type codes are UM, UT, DM, DD and PP.",)}
		//Another Error - {navigate=(13297246,1.1545800000,13297198,48.5846800000,310,"Start ComID must have a hydroseq greater than the hydroseq for stop ComID.",{f170f490-00ad-11e6-8f62-0242ac110003})}
		//A Good Result - {navigate=(13297246,0.0000000000,,,0,,{4d06cca2-001e-11e6-b9d0-0242ac110003})}
		LOG.debug("return from navigate:" + navigationResult.get(NavigationDao.NAVIGATE).toString());

		String sessionId = null;
		String resultCode = null;
		String statusMessage = null;

		try {
			String resultCsv = navigationResult.get(NavigationDao.NAVIGATE).toString().replace("(", "").replace(")", "");
//			String resultCsv = navigationResult.get("navigate_vpu_core").toString().replace("(", "").replace(")", "");
			CsvMapper mapper = new CsvMapper();
			mapper.enable(CsvParser.Feature.WRAP_AS_ARRAY);
			MappingIterator<String[]> mi = mapper.readerFor(String[].class).readValues(resultCsv);
			while (mi.hasNext()) {
				String[] result = mi.next();

				resultCode = result[4];
				statusMessage = result[5];
				sessionId = result[6];
//				resultCode = result[15];
//				statusMessage = result[16];
				if (!"0".equals(resultCode)) {
					String msg = "{\"errorCode\":" + resultCode + ", \"errorMessage\":\"" + statusMessage + "\"}";
					LOG.debug(msg);
					responseStream.write(msg.getBytes());
				}
			}
		} catch (Throwable e) {
			LOG.error("Unable to stream error message", e);
		}

//		parameterMap.put("returnCode", Integer.valueOf(resultCode));
//		parameterMap.put("statusMessage", statusMessage);
//		navigationDao.insertCache(parameterMap);
		return sessionId;
	}

}
