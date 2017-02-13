package com.example.javaconfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;

// <1>
@Configuration
public class ApplicationConfiguration {

	// <2>
	@Bean(destroyMethod = "shutdown")
	DataSource dataSource() {
		return new EmbeddedDatabaseBuilder()
				.setType(EmbeddedDatabaseType.H2)
				.setName("customers")
				.build();
	}

	// <3>
	@Bean
	CustomerService customerService(DataSource dataSource) {
		return new CustomerService(dataSource);
	}
}
