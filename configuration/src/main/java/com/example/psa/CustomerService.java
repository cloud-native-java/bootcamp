package com.example.psa;

import com.example.Customer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class CustomerService {

	private final JdbcTemplate jdbcTemplate;

	public CustomerService(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public Collection<Customer> findAll() {
		// <1>
		RowMapper<Customer> rowMapper =
				(rs, i) -> new Customer(rs.getLong("ID"), rs.getString("EMAIL"));
		// <2>
		return this.jdbcTemplate.query("select * from CUSTOMERS ", rowMapper);
	}
}
