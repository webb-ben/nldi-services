package gov.usgs.owi.nldi.controllers;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.ResultHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.util.NumberUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import gov.usgs.owi.nldi.dao.StreamingDao;
import gov.usgs.owi.nldi.dao.StreamingResultHandler;
import gov.usgs.owi.nldi.transform.ITransformer;
import gov.usgs.owi.nldi.transform.MapToJsonTransformer;

@Controller
public class RestController {
	private static final Logger LOG = LoggerFactory.getLogger(RestController.class);
	
	public static final String COMID = "comid";
	public static final String NAVIGATION_MODE = "navigationMode";
	public static final String STOP_COMID = "stopComid";
	public static final String DISTANCE = "distance";

	public static final String NAVIGATE = "navigate";
	public static final String SESSION_ID = "sessionId";

	protected final StreamingDao streamingDao;

	@Autowired
	public RestController(StreamingDao inStreamingDao) {
		streamingDao = inStreamingDao;
	}

	@RequestMapping(value="/comid/{comid}/navigate/{navigationMode}", method=RequestMethod.GET)
    public void navigation(HttpServletRequest request, HttpServletResponse response,
    		@PathVariable(COMID) String comid,
    		@PathVariable(NAVIGATION_MODE) String navigationMode,
    		@RequestParam(value=STOP_COMID, required=false) String stopComid,
    		@RequestParam(value=DISTANCE, required=false) String distance) {
		OutputStream responseStream = null;

		try {
			responseStream = new BufferedOutputStream(response.getOutputStream());
			Map<String, Object> parameterMap = new HashMap<> ();
			
			if (StringUtils.isNotBlank(comid)) {
				parameterMap.put(COMID, NumberUtils.parseNumber(comid, Integer.class));
			}
			if (StringUtils.isNotBlank(navigationMode)) {
				parameterMap.put(NAVIGATION_MODE, navigationMode);
			}
			if (StringUtils.isNotBlank(stopComid)) {
				parameterMap.put(STOP_COMID, NumberUtils.parseNumber(stopComid, Integer.class));
			}
			if (StringUtils.isNotBlank(distance)) {
				parameterMap.put(DISTANCE, NumberUtils.parseNumber(distance, BigDecimal.class));
			}
						
			LinkedHashMap<?,?> navigationResult = streamingDao.navigate(NAVIGATE, parameterMap);
			//  -  type="record" value="(13297246,0.0000000000,,,0,,{f8612242-ea24-11e5-9999-0242ac110003})"
			
			String[] result = navigationResult.get(NAVIGATE).toString().split(",");
			
			if ("0".equals(result[4])) {
				parameterMap.put(SESSION_ID, result[6].replace(")", ""));
				streamResults(responseStream, parameterMap);
			} else {
				response.setStatus(HttpStatus.BAD_REQUEST.value());
				String msg = "{\"error\":" + result[5] + "}";
				responseStream.write(msg.getBytes());
			}

		} catch (Exception e) {
			LOG.error("Handle me better" + e.getLocalizedMessage(), e);
		} finally {
			if (null != responseStream) {
				try {
					responseStream.flush();
				} catch (IOException e) {
					//Just log, cause we obviously can't tell the client
					LOG.error("Error flushing response stream", e);
				}
			}
		}
    }

	protected void streamResults(OutputStream responseStream, Map<String, Object> parameterMap) {
		
		ITransformer transformer = new MapToJsonTransformer(responseStream);
		
		ResultHandler<?> handler = new StreamingResultHandler(transformer);
		streamingDao.stream(NAVIGATE, parameterMap, handler);

		transformer.end();		
	}
	
}
