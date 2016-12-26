package com.example.noraai;

import com.example.Customer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CustomerService {

	private final DataSource dataSource =
			new EmbeddedDatabaseBuilder()
					.setName("customers")
					.setType(EmbeddedDatabaseType.H2)
					.build();

	public Customer findById(Long id) {
		List<Customer> customerList = new ArrayList<>();
		try {
			try (Connection c = dataSource.getConnection()) {
				Statement statement = c.createStatement();
				try (ResultSet resultSet = statement.executeQuery("select * from CUSTOMERS")) {
					while (resultSet.next()) {
						customerList.add(new Customer(
								resultSet.getLong("ID"),
								resultSet.getString("EMAIL")
						));
					}
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return customerList.iterator().next();
	}

	public static void main(String argsp[]) throws Throwable {
		Log log = LogFactory.getLog(CustomerService.class);
		CustomerService customerService = new CustomerService();
		DataSource dataSource = customerService.dataSource;
		DataSourceInitializer init = new DataSourceInitializer();
		init.setDataSource(dataSource);
		ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
		populator.setScripts(new ClassPathResource("schema.sql"),
				new ClassPathResource("data.sql"));
		init.setDatabasePopulator(populator);
		init.afterPropertiesSet();
		Customer byId = customerService.findById(1L);
		Assert.notNull(byId, "the customer should be discoverable");
		log.info("byId: " + byId);
	}
}
