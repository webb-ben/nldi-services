package gov.usgs.owi.nldi.springinit;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

@Configuration
@Import(MybatisConfig.class)
@ComponentScan(basePackages={"gov.usgs.owi.nldi.controllers", "gov.usgs.owi.nldi.dao", "gov.usgs.owi.nldi.services", "gov.usgs.owi.nldi.transform"})
@EnableWebMvc
public class SpringConfig extends WebMvcConfigurerAdapter {

	@Autowired
	DataSource dataSource;

	@Override
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
		configurer.enable();
	}

	@Bean
	public InternalResourceViewResolver setupViewResolver() {
		InternalResourceViewResolver resolver = new InternalResourceViewResolver();
		resolver.setPrefix("/WEB-INF/views/");
		resolver.setSuffix(".jsp");
		return resolver;
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("swagger-ui.html", "webjars/*")
			.addResourceLocations("classpath:/META-INF/resources/");

		registry.setOrder(-1);
	}

}
