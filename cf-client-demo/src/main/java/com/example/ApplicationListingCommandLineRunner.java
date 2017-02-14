package com.example;

import org.cloudfoundry.operations.CloudFoundryOperations;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
class ApplicationListingCommandLineRunner implements CommandLineRunner {

	private final CloudFoundryOperations cf; // <1>

	ApplicationListingCommandLineRunner(CloudFoundryOperations cf) {
		this.cf = cf;
	}

	@Override
	public void run(String... args) throws Exception {
		cf.applications().list().subscribe(System.out::println); // <2>
	}
}
