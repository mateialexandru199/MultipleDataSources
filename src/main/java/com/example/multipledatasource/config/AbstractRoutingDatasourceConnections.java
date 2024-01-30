package com.example.multipledatasource.config;

import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class AbstractRoutingDatasourceConnections extends AbstractRoutingDataSource {

	private static final ThreadLocal<String> DATABASE_NAME = new ThreadLocal<>();

	public AbstractRoutingDatasourceConnections(DataSource defaultTargetDatasource, Map<Object, Object> targetDataSources) {
		super.setDefaultTargetDataSource(defaultTargetDatasource);
		super.setTargetDataSources(targetDataSources);
		super.afterPropertiesSet();
	}

	public static void setDatabaseName(String key) {
		DATABASE_NAME.set(key);
	}

	public static String getDatabaseName() {
		return DATABASE_NAME.get();
	}

	@Override
	protected Object determineCurrentLookupKey() {
		return DATABASE_NAME.get();
	}
}
