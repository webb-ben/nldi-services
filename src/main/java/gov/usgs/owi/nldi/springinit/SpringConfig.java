package gov.usgs.owi.nldi.springinit;

import gov.usgs.owi.nldi.controllers.BaseController;
import gov.usgs.owi.nldi.converters.ComidMessageConverter;
import gov.usgs.owi.nldi.converters.ErrorMessageConverter;
import gov.usgs.owi.nldi.converters.FeatureListMessageConverter;
import gov.usgs.owi.nldi.converters.FeatureMessageConverter;
import java.util.List;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.*;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

@Configuration
@EnableWebMvc
public class SpringConfig implements WebMvcConfigurer {

  @Override
  public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
    configurer.enable();
  }

  @Bean
  WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> enableDefaultServlet() {
    return (factory) -> factory.setRegisterDefaultServlet(true);
  }

  @Bean
  public InternalResourceViewResolver setupViewResolver() {
    InternalResourceViewResolver resolver = new InternalResourceViewResolver();
    resolver.setPrefix("/WEB-INF/views/");
    resolver.setSuffix(".jsp");
    return resolver;
  }

  @Override
  public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
    configurer
        .ignoreAcceptHeader(false)
        .favorParameter(true)
        .parameterName("f")
        .mediaType("jsonld", MediaType.valueOf(BaseController.MIME_TYPE_JSONLD))
        .mediaType("json", MediaType.APPLICATION_JSON)
        .mediaType("geojson", MediaType.valueOf(BaseController.MIME_TYPE_GEOJSON))
        .mediaType("html", MediaType.TEXT_HTML)
        .defaultContentType(MediaType.APPLICATION_JSON);
  }

  @Override
  public void extendMessageConverters(List<HttpMessageConverter<?>> messageConverters) {
    MediaType geoJson = MediaType.valueOf(BaseController.MIME_TYPE_GEOJSON);
    MediaType jsonLd = MediaType.valueOf(BaseController.MIME_TYPE_JSONLD);
    // we want our converters to take priority, so we add them to the top of the list
    messageConverters.add(0, new ComidMessageConverter(geoJson, MediaType.APPLICATION_JSON));
    messageConverters.add(
        0, new FeatureListMessageConverter(geoJson, jsonLd, MediaType.APPLICATION_JSON));
    messageConverters.add(
        0, new FeatureMessageConverter(geoJson, jsonLd, MediaType.APPLICATION_JSON));
    messageConverters.add(0, new ErrorMessageConverter(MediaType.ALL));
  }

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry
        .addMapping("/**")
        .allowedOrigins("*")
        .allowedMethods("GET", "OPTIONS")
        .allowedHeaders(
            "Origin",
            "Accept",
            "X-Requested-With",
            "Content-Type",
            "Access-Control-Request-Method",
            "Access-Control-Request-Headers");
  }
}
