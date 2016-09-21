package gov.usgs.owi.nldi.springinit;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
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
	public String confluenceUrl() throws NamingException {
		return (String) ctx.lookup("java:comp/env/nldi/confluenceUrl");
	}

	@Bean
	public String displayProtocol() throws NamingException {
		return (String) ctx.lookup("java:comp/env/nldi/displayProtocol");
	}

	@Bean
	public String displayHost() throws NamingException {
		return (String) ctx.lookup("java:comp/env/nldi/displayHost");
	}

	@Bean
	public String displayPath() throws NamingException {
		return (String) ctx.lookup("java:comp/env/nldi/displayPath");
	}
	@Bean
	public String rootUrl() throws NamingException {
		return displayProtocol() + "://" + displayHost() + displayPath();
	}

}
