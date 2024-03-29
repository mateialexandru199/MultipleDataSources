package com.example.multipledatasource.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jndi.JndiTemplate;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Configuration
@EnableJpaRepositories(basePackages = "com.example.multipledatasource",entityManagerFactoryRef = "entityManager")
public class DynamicDatabaseRouter {

	public static final String PROPERTY_PREFIX = "spring.datasource.";

	@Autowired
	private Environment environment;

	@Bean
	@Primary
	@Scope("prototype")
	public AbstractRoutingDatasourceConnections dataSources() {
		Map<Object, Object> targetDataSources = getTargetDataSources();
		return new AbstractRoutingDatasourceConnections((DataSource)targetDataSources.get("default"), targetDataSources);
	}

	@Bean(name = "entityManager")
	@Scope("prototype")
	public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean(EntityManagerFactoryBuilder builder) {
		return builder.dataSource(dataSources()).packages("com.example.multipledatasource").build();
	}

	private Map<Object,Object> getTargetDataSources() {

		//loading the database names to a list from application.properties file
		List<String> databaseNames = environment.getProperty("spring.database-names.list",List.class);
		Map<Object,Object> targetDataSourceMap = new HashMap<>();

		for (String dbName : databaseNames) {
			//for jndi case we will need to use " new JndiTemplate().lookup(environment.getProperty("jdbc.url")); "
			DriverManagerDataSource dataSource = new DriverManagerDataSource();
			dataSource.setDriverClassName(Objects.requireNonNull(environment.getProperty(PROPERTY_PREFIX + dbName + ".driver")));
			dataSource.setUrl(environment.getProperty(PROPERTY_PREFIX + dbName + ".url"));
			dataSource.setUsername(environment.getProperty(PROPERTY_PREFIX + dbName + ".username"));
			dataSource.setPassword(environment.getProperty(PROPERTY_PREFIX + dbName + ".password"));
			targetDataSourceMap.put(dbName,dataSource);
		}
		targetDataSourceMap.put("default",targetDataSourceMap.get(databaseNames.get(0)));
		return targetDataSourceMap;
	}
}