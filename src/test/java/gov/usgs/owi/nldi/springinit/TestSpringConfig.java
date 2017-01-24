package gov.usgs.owi.nldi.springinit;

import javax.naming.NamingException;
import javax.sql.DataSource;

import org.dbunit.ext.postgresql.PostgresqlDataTypeFactory;
import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import com.github.springtestdbunit.bean.DatabaseConfigBean;
import com.github.springtestdbunit.bean.DatabaseDataSourceConnectionFactoryBean;

@Configuration
@Import(MybatisConfig.class)
@PropertySource(value = "classpath:test.properties")
public class TestSpringConfig implements EnvironmentAware {

	public static final String TEST_ROOT_URL = "http://owi-test.usgs.gov:8080/test-url";
	private Environment env;

	@Bean
	public DataSource dataSource() throws Exception {
		PGSimpleDataSource ds = new PGSimpleDataSource();
		ds.setUrl(env.getProperty("jdbc.nldi.url"));
		ds.setUser(env.getProperty("jdbc.nldi.username"));
		ds.setPassword(env.getProperty("jdbc.nldi.password"));
		return ds;
	}

	@Bean
	public DataSource dbUnitDataSource() throws Exception {
		PGSimpleDataSource ds = new PGSimpleDataSource();
		ds.setUrl(env.getProperty("jdbc.nldi.url"));
		ds.setUser(env.getProperty("jdbc.dbunit.username"));
		ds.setPassword(env.getProperty("jdbc.dbunit.password"));
		return ds;
	}

	@Override
	public void setEnvironment(Environment environment) {
		env = environment;
	}

	@Bean
	public String rootUrl() throws NamingException {
		return TEST_ROOT_URL;
	}

	@Bean
	public String confluenceUrl() throws NamingException {
		return TEST_ROOT_URL;
	}

	//Beans to support DBunit for unit testing with PostgreSQL.
	@Bean
	public DatabaseConfigBean dbUnitDatabaseConfig() {
		DatabaseConfigBean dbUnitDbConfig = new DatabaseConfigBean();
		dbUnitDbConfig.setDatatypeFactory(new PostgresqlDataTypeFactory());
		dbUnitDbConfig.setQualifiedTableNames(true);
		return dbUnitDbConfig;
	}

	@Bean
	public DatabaseDataSourceConnectionFactoryBean dbUnitDatabaseConnection() throws Exception {
		DatabaseDataSourceConnectionFactoryBean dbUnitDatabaseConnection = new DatabaseDataSourceConnectionFactoryBean();
		dbUnitDatabaseConnection.setDatabaseConfig(dbUnitDatabaseConfig());
		dbUnitDatabaseConnection.setDataSource(dbUnitDataSource());
		return dbUnitDatabaseConnection;
	}

}
