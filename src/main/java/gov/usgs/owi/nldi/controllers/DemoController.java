package gov.usgs.owi.nldi.controllers;

import gov.usgs.owi.nldi.dao.DemoStreamingDao;
import gov.usgs.owi.nldi.dao.StreamingResultHandler;
import gov.usgs.owi.nldi.transform.FlowLineTransformer;
import gov.usgs.owi.nldi.transform.ITransformer;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.session.ResultHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class DemoController {
	private static final Logger LOG = LoggerFactory.getLogger(RestController.class);

	protected final DemoStreamingDao streamingDao;

	@Autowired
	public DemoController(DemoStreamingDao inStreamingDao) {
		streamingDao = inStreamingDao;
	}

	@RequestMapping(value="/", method=RequestMethod.GET)
    public ModelAndView entry() {
		ModelAndView mv = new ModelAndView("index.jsp");
		return mv;
    }

	@RequestMapping(value="/navigation", method=RequestMethod.GET)
    public void navigation(HttpServletRequest request, HttpServletResponse response) {
		OutputStream responseStream = null;

		try {
			responseStream = new BufferedOutputStream(response.getOutputStream());
			Map<String, Object> parameterMap = new HashMap<> ();
			
			ITransformer transformer = new FlowLineTransformer(responseStream);
			
			LinkedHashMap<?,?> navigationResult = streamingDao.navigate("demoNavigate", null);
			String[] result = navigationResult.get("navigate").toString().split(",");
			parameterMap.put("sessionId", result[6].replace(")", ""));
			
			ResultHandler<?> handler = new StreamingResultHandler(transformer);
			streamingDao.stream("demoNavigate", parameterMap, handler);
	
			transformer.end();

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

}
