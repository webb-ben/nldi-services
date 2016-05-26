package gov.usgs.owi.nldi.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import gov.usgs.owi.nldi.ApplicationVersion;

@Controller
public class DemoController {

	@RequestMapping(value="/demo", method=RequestMethod.GET)
	public ModelAndView entry() {
		
		ModelAndView mv = new ModelAndView("demo.jsp");
		mv.addObject("version", ApplicationVersion.getVersion());

		return mv;
	}

}
