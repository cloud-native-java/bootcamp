package com.example.componentscan;

import com.example.Customer;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
public class CustomerService {

	private final DataSource dataSource;

	public CustomerService(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public Collection<Customer> findAll() {
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
		return customerList;
	}
}
