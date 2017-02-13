package bootcamp;

import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.applications.PushApplicationRequest;
import org.cloudfoundry.operations.applications.SetEnvironmentVariableApplicationRequest;
import org.cloudfoundry.operations.applications.StartApplicationRequest;
import org.cloudfoundry.operations.services.BindServiceInstanceRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.time.Duration;
import java.util.stream.Stream;

class ApplicationDeployer {

	private final CloudFoundryOperations cf;

	ApplicationDeployer(CloudFoundryOperations cf) {
		this.cf = cf;
	}

	Mono<Void> deployApplication(File jar, String applicationName, String... svcs) {

		return cf.applications()
				.push(PushApplicationRequest.builder()
						.name(applicationName)
						.noStart(true)
						.randomRoute(true)
						.buildpack("https://github.com/cloudfoundry/java-buildpack.git")
						.application(jar.toPath())
						.instances(1)
						.build())
				.thenMany(Flux.concat(Flux.fromStream(Stream.of(svcs)
						.map(svc ->
								cf.services()
										.bind(BindServiceInstanceRequest.builder()
												.applicationName(applicationName)
												.serviceInstanceName(svc)
												.build())))))
				.then()
				.then(cf.applications()
						.setEnvironmentVariable(SetEnvironmentVariableApplicationRequest.builder()
								.name(applicationName)
								.variableName("SPRING_PROFILES_ACTIVE")
								.variableValue("cloud")
								.build()))
				.then(cf.applications()
						.start(StartApplicationRequest.builder()
								.name(applicationName)
								.stagingTimeout(Duration.ofMinutes(5))
								.startupTimeout(Duration.ofMinutes(5))
								.build()));
	}
}
