package gov.usgs.owi.nldi.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import gov.usgs.owi.nldi.dao.CountDao;
import gov.usgs.owi.nldi.dao.StreamingDao;
import gov.usgs.owi.nldi.services.Navigation;

@Controller
@RequestMapping(value="/comid/{comid}/navigate/{navigationMode}")
public class NetworkController extends BaseController {

	@Autowired
	public NetworkController(CountDao inCountDao, StreamingDao inStreamingDao, Navigation inNavigation,
			@Qualifier("rootUrl") String inRootUrl) {
		super(inCountDao, inStreamingDao, inNavigation, inRootUrl);
	}

	@RequestMapping(method=RequestMethod.GET)
	public void getFlowlines(HttpServletRequest request, HttpServletResponse response,
			@PathVariable(Navigation.COMID) String comid,
			@PathVariable(Navigation.NAVIGATION_MODE) String navigationMode,
			@RequestParam(value=Navigation.STOP_COMID, required=false) String stopComid,
			@RequestParam(value=Navigation.DISTANCE, required=false) String distance) {
		streamFlowLines(response, comid, navigationMode, stopComid, distance);
	}

	@RequestMapping(value="{dataSource}", method=RequestMethod.GET)
	public void getFeatures(HttpServletRequest request, HttpServletResponse response,
			@PathVariable(Navigation.COMID) String comid,
			@PathVariable(Navigation.NAVIGATION_MODE) String navigationMode,
			@PathVariable(value=DATA_SOURCE) String dataSource,
			@RequestParam(value=Navigation.STOP_COMID, required=false) String stopComid,
			@RequestParam(value=Navigation.DISTANCE, required=false) String distance) {
		streamFeatures(response, comid, navigationMode, stopComid, distance, dataSource);
	}

}
