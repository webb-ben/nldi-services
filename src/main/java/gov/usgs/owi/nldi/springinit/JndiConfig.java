package gov.usgs.owi.nldi.springinit;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class JndiConfig {

	private final Context ctx;
	
	public JndiConfig() throws NamingException {
		ctx = new InitialContext();
	}

	@Bean
	public DataSource dataSource() throws Exception {
		return (DataSource) ctx.lookup("java:comp/env/jdbc/NLDI");
	}

	@Bean
	public String rootUrl() throws NamingException {
		return (String) ctx.lookup("java:comp/env/nldi/displayUrl");
	}

	@Bean
	public String confluenceUrl() throws NamingException {
		return (String) ctx.lookup("java:comp/env/nldi/confluenceUrl");
	}

}
