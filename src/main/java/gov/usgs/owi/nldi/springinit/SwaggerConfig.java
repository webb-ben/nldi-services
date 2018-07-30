package gov.usgs.owi.nldi.springinit;

import gov.usgs.owi.nldi.services.ConfigurationService;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import springfox.documentation.PathProvider;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.paths.AbstractPathProvider;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@Profile("swagger")
public class SwaggerConfig {

	@Autowired
	private ConfigurationService configurationService;
	
	@Bean
	public Docket nldiServicesApi() {
		Set<String> protocols = new HashSet<>();
		protocols.add(configurationService.getDisplayProtocol());
		return new Docket(DocumentationType.SWAGGER_2)
				.protocols(protocols)
				.host(configurationService.getDisplayHost())
				.pathProvider(pathProvider());
	}

	@Bean
	public PathProvider pathProvider() {
		PathProvider rtn = new ProxyPathProvider();
		return rtn;
	}

	public class ProxyPathProvider extends AbstractPathProvider {
		@Override
		protected String applicationPath() {
			return configurationService.getDisplayPath();
		}
	
		@Override
		protected String getDocumentationPath() {
			return configurationService.getDisplayPath();
		}
	}
}
