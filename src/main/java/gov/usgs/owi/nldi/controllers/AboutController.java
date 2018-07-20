package gov.usgs.owi.nldi.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import gov.usgs.owi.nldi.AtomReaderUtil;
import gov.usgs.owi.nldi.services.ApplicationVersion;
import gov.usgs.owi.nldi.services.ConfigurationService;

@Controller
@RequestMapping(value="about")
public class AboutController {

	protected ConfigurationService configurationService;

	@Autowired
	public AboutController(ConfigurationService configurationService) {
		this.configurationService = configurationService;
	}

	@GetMapping
	public ModelAndView getIndex() {
		ModelAndView mv = new ModelAndView("about");
		mv.addObject("version", ApplicationVersion.getVersion());
		mv.addObject(
				"userGuide", 
				AtomReaderUtil.getAtomFeedContentOnlyAsString(configurationService.getConfluenceUrl()));
		return mv;
	}

	@GetMapping(value="{requestedPage}")
	public ModelAndView getpage(@PathVariable("requestedPage") String requestedPage) {
		ModelAndView mv = new ModelAndView(requestedPage);
		mv.addObject("version", ApplicationVersion.getVersion());
		mv.addObject(
				"userGuide", 
				AtomReaderUtil.getAtomFeedContentOnlyAsString(configurationService.getConfluenceUrl()));
		return mv;
	}

}
