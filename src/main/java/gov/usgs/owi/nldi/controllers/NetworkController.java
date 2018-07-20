package gov.usgs.owi.nldi.controllers;

import java.io.IOException;
import java.math.BigInteger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gov.usgs.owi.nldi.dao.LookupDao;
import gov.usgs.owi.nldi.dao.StreamingDao;
import gov.usgs.owi.nldi.services.ConfigurationService;
import gov.usgs.owi.nldi.services.LogService;
import gov.usgs.owi.nldi.services.Navigation;
import gov.usgs.owi.nldi.services.Parameters;

@RestController
@RequestMapping(value="/comid/{comid}/navigate/{navigationMode}")
public class NetworkController extends BaseController {
	
	protected ConfigurationService configurationService;

	@Autowired
	public NetworkController(LookupDao inLookupDao, StreamingDao inStreamingDao,
			Navigation inNavigation, Parameters inParameters, ConfigurationService configurationService,
			LogService inLogService) {
		super(inLookupDao, inStreamingDao, inNavigation, inParameters, configurationService.getRootUrl(), inLogService);
	}

	@GetMapping
	public void getFlowlines(HttpServletRequest request, HttpServletResponse response,
			@PathVariable(Parameters.COMID) String comid,
			@PathVariable(Parameters.NAVIGATION_MODE) String navigationMode,
			@RequestParam(value=Parameters.STOP_COMID, required=false) String stopComid,
			@RequestParam(value=Parameters.DISTANCE, required=false) String distance,
			@RequestParam(value=Parameters.LEGACY, required=false) String legacy) throws IOException {
		BigInteger logId = logService.logRequest(request);
		streamFlowLines(response, comid, navigationMode, stopComid, distance, isLegacy(legacy, navigationMode));
		logService.logRequestComplete(logId, response.getStatus());
	}

	@GetMapping(value="{dataSource}")
	public void getFeatures(HttpServletRequest request, HttpServletResponse response,
			@PathVariable(Parameters.COMID) String comid,
			@PathVariable(Parameters.NAVIGATION_MODE) String navigationMode,
			@PathVariable(value=DATA_SOURCE) String dataSource,
			@RequestParam(value=Parameters.STOP_COMID, required=false) String stopComid,
			@RequestParam(value=Parameters.DISTANCE, required=false) String distance,
			@RequestParam(value=Parameters.LEGACY, required=false) String legacy) throws IOException {
		BigInteger logId = logService.logRequest(request);
		streamFeatures(response, comid, navigationMode, stopComid, distance, dataSource, isLegacy(legacy, navigationMode));
		logService.logRequestComplete(logId, response.getStatus());
	}

}
