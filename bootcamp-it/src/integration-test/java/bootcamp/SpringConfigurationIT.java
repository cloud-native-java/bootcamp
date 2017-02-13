package bootcamp;

import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.applications.PushApplicationRequest;
import org.cloudfoundry.operations.applications.SetEnvironmentVariableApplicationRequest;
import org.cloudfoundry.operations.applications.StartApplicationRequest;
import org.cloudfoundry.operations.services.BindServiceInstanceRequest;
import org.cloudfoundry.operations.services.CreateServiceInstanceRequest;
import org.cloudfoundry.operations.services.DeleteServiceInstanceRequest;
import org.cloudfoundry.operations.services.UnbindServiceInstanceRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import reactor.core.publisher.Mono;

import java.io.File;
import java.time.Duration;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SpringConfigurationIT.Config.class)
public class SpringConfigurationIT {

	@Autowired
	private CloudFoundryOperations cf;

	@Test
	public void deploy() throws Throwable {

		File projectFolder = new File(
				new File("."), "../spring-configuration");

		File jar = new File(projectFolder, "target/spring-configuration.jar");

		String applicationName = "bootcamp-customers";
		String mysqlServiceName = "bootcamp-customers-mysql";

		// first run, lets find any existing services and unbind them if they exist
		Mono<Void> services = cf.services()
				.listInstances()
				.filter(si -> si.getName().equalsIgnoreCase(mysqlServiceName))
				.singleOrEmpty()
				.then(serviceInstance ->
						cf.services()
								.unbind(UnbindServiceInstanceRequest.builder()
										.applicationName(applicationName)
										.serviceInstanceName(mysqlServiceName)
										.build())
								.then(cf.services()
										.deleteInstance(DeleteServiceInstanceRequest.builder()
												.name(serviceInstance.getName())
												.build())))
				.then(cf.services()
						.createInstance(CreateServiceInstanceRequest.builder()
								.serviceName("p-mysql")
								.planName("100mb")
								.serviceInstanceName(mysqlServiceName)
								.build()));

		Mono<Void> apps = cf.applications()
				.push(PushApplicationRequest.builder()
						.name(applicationName)
						.noStart(true)
						.randomRoute(true)
						.buildpack("https://github.com/cloudfoundry/java-buildpack.git")
						.application(jar.toPath())
						.instances(1)
						.build())
				.then(cf.services()
						.bind(BindServiceInstanceRequest.builder()
								.applicationName(applicationName)
								.serviceInstanceName(mysqlServiceName)
								.build()))
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


		services.then(apps).block();

	}

	@SpringBootApplication
	public static class Config {
	}
}
