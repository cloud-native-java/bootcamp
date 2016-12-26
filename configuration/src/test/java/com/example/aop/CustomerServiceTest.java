package com.example.aop;


import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.rule.OutputCapture;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;

import static org.hamcrest.Matchers.containsString;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {ApplicationConfiguration.class,
		CustomerServiceTest.CustomConfiguration.class})
public class CustomerServiceTest {

	@Autowired
	private CustomerService customerService;

	@Rule
	public OutputCapture outputCapture = new OutputCapture();

	@Configuration
	public static class CustomConfiguration {

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

	@Test
	public void findAll() throws Exception {
		int size = this.customerService.findAll().size();
		org.junit.Assert.assertEquals(size, 2);
		String consoleOutput = this.outputCapture.toString();
		org.junit.Assert.assertThat(consoleOutput, containsString("starting @"));
		org.junit.Assert.assertThat(consoleOutput, containsString("finishing @"));
	}
}