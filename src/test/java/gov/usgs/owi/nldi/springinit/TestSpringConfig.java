package gov.usgs.owi.nldi.springinit;

import javax.sql.DataSource;

import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@Configuration
@PropertySource(value = "classpath:test.properties")
public class TestSpringConfig implements EnvironmentAware {

	private Environment env;

    @Bean
    public DataSource dataSource() throws Exception {
    	PGSimpleDataSource ds = new PGSimpleDataSource();
		ds.setUrl(env.getProperty("jdbc.nldi.url"));
		ds.setUser(env.getProperty("jdbc.nldi.username"));
		ds.setPassword(env.getProperty("jdbc.nldi.password"));
		return ds;
	}

	@Override
	public void setEnvironment(Environment environment) {
		env = environment;
	}

}
