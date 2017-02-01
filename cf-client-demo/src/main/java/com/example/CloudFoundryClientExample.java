package com.example;

import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.DefaultCloudFoundryOperations;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.DefaultConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.client.ReactorCloudFoundryClient;
import org.cloudfoundry.reactor.doppler.ReactorDopplerClient;
import org.cloudfoundry.reactor.tokenprovider.PasswordGrantTokenProvider;
import org.cloudfoundry.reactor.uaa.ReactorUaaClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;


@SpringBootApplication
public class CloudFoundryClientExample {

	@Bean
	ReactorCloudFoundryClient cloudFoundryClient(
			ConnectionContext connectionContext,
			TokenProvider tokenProvider) {
		return ReactorCloudFoundryClient.builder()
				.connectionContext(connectionContext)
				.tokenProvider(tokenProvider)
				.build();
	}

	@Bean
	ReactorDopplerClient dopplerClient(
			ConnectionContext connectionContext,
			TokenProvider tokenProvider) {
		return ReactorDopplerClient.builder()
				.connectionContext(connectionContext)
				.tokenProvider(tokenProvider)
				.build();
	}

	@Bean
	ReactorUaaClient uaaClient(
			ConnectionContext connectionContext,
			TokenProvider tokenProvider) {
		return ReactorUaaClient.builder()
				.connectionContext(connectionContext)
				.tokenProvider(tokenProvider)
				.build();
	}

	@Bean
	DefaultConnectionContext connectionContext(@Value("${cf.api}") String apiHost) {
		if (apiHost.contains("://")) {
			apiHost = apiHost.split("://")[1];
		}
		return DefaultConnectionContext.builder()
				.apiHost(apiHost)
				.build();
	}

	@Bean
	PasswordGrantTokenProvider tokenProvider(@Value("${cf.user}") String username,
	                                         @Value("${cf.password}") String password) {
		return PasswordGrantTokenProvider.builder()
				.password(password)
				.username(username)
				.build();
	}

	@Bean
	DefaultCloudFoundryOperations cloudFoundryOperations(
			CloudFoundryClient cloudFoundryClient,
			ReactorDopplerClient dopplerClient,
			ReactorUaaClient uaaClient,
			@Value("${cf.org}") String organization,
			@Value("${cf.space}") String space) {
		return DefaultCloudFoundryOperations.builder()
				.cloudFoundryClient(cloudFoundryClient)
				.dopplerClient(dopplerClient)
				.uaaClient(uaaClient)
				.organization(organization)
				.space(space)
				.build();
	}

	@Bean
	CommandLineRunner applicationRunner(
		 CloudFoundryOperations ops ) {
		return args -> {
			ops.applications().list().subscribe(System.out::println);
		} ;
	}

	public static void main(String[] args) {
		SpringApplication.run(CloudFoundryClientExample.class, args);
	}
}
//
//
//@Component
//class Promoter {
//
//	private final CloudFoundryOperations cloudFoundryClient;
//
//	Promoter(CloudFoundryOperations cloudFoundryClient) {
//		this.cloudFoundryClient = cloudFoundryClient;
//	}
//
//	void promote(String appName) {
//
//		String live = appName + "-live", staging = appName + "-staging";
//
//		Log log = LogFactory.getLog(getClass());
//		try {
//			this.cloudFoundryClient.routes()
//					.unmap(UnmapRouteRequest.builder()
//							.applicationName(appName)
//							.domain("cfapps.io")
//							.host(staging)
//							.build())
//					.block();
//		} catch (Throwable t) {
//			log.error(t);
//		}
//
//		try {
//			this.cloudFoundryClient.routes()
//					.map(MapRouteRequest.builder()
//							.applicationName(appName)
//							.domain("cfapps.io")
//							.host(live)
//							.build())
//					.block();
//		} catch (Throwable t) {
//			log.error(t);
//		}
//	}
//}