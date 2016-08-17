package gov.usgs.owi.nldi.springinit;

import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration.Dynamic;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import gov.usgs.owi.nldi.gzip.GZIPFilter;

public class SpringInitializer implements WebApplicationInitializer {

	/**
	 *  gets invoked automatically when application context loads
	 */
	public void onStartup(ServletContext servletContext) throws ServletException {		
		AnnotationConfigWebApplicationContext ctx = new AnnotationConfigWebApplicationContext();
		ctx.register(SpringConfig.class, JndiConfig.class);

		FilterRegistration gzipFilter = servletContext.addFilter("gzipFilter", GZIPFilter.class);
		gzipFilter.addMappingForUrlPatterns(null, true, "/*");

		Dynamic servlet = servletContext.addServlet("springDispatcher", new DispatcherServlet(ctx));
		servlet.addMapping("/");
		servlet.setAsyncSupported(false);
		servlet.setLoadOnStartup(1);
	}

}