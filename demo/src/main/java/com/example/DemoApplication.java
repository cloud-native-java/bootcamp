package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication // <1>
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args); // <2>
	}

	@RestController // <3>
	public static class HelloRestController {

		@RequestMapping(value = "/hello") // <4>
		String hello(
				@RequestParam(value = "name", defaultValue = "World") String name) { // <5>
			return "Hello, " + name;
		}
	}
}
