package com.example.boot;

import com.example.Customer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController // <1>
public class CustomerRestController {

	private final CustomerService customerService;

	public CustomerRestController(CustomerService customerService) {
		this.customerService = customerService;
	}

	// <2>
	@GetMapping("/customers")
	public Collection<Customer> readAll() {
		return this.customerService.findAll();
	}
}
