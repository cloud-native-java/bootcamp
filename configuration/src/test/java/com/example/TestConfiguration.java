package com.example;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;

@Configuration
public class TestConfiguration {

	@Bean
	public DataSourceInitializer init(DataSource dataSource) {
		DataSourceInitializer init = new DataSourceInitializer();
		init.setDataSource(dataSource);
		ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
		populator.setScripts(new ClassPathResource("schema.sql"),
				new ClassPathResource("data.sql"));
		init.setDatabasePopulator(populator);
		return init;
	}
}
