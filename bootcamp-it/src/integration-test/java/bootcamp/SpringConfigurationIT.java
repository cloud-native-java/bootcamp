package bootcamp;

import org.cloudfoundry.operations.CloudFoundryOperations;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import reactor.core.publisher.Mono;

import java.io.File;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SpringConfigurationIT.Config.class)
public class SpringConfigurationIT {

	@Autowired
	private CloudFoundryOperations cf;

	@Autowired
	private ApplicationDeployer applicationDeployer;

	@Autowired
	private ServicesDeployer servicesDeployer;

	@Test
	public void deploy() throws Throwable {
		File projectFolder = new File(new File("."), "../spring-configuration");
		File jar = new File(projectFolder, "target/spring-configuration.jar");
		String applicationName = "bootcamp-customers";
		String mysqlServiceName = "bootcamp-customers-mysql";
		Mono<Void> service = servicesDeployer.deployService(
				applicationName, mysqlServiceName);
		Mono<Void> apps = applicationDeployer.deployApplication(
				jar, applicationName, mysqlServiceName);
		service.then(apps).block();
	}

	@SpringBootApplication
	public static class Config {

		@Bean
		ApplicationDeployer applications(CloudFoundryOperations cloudFoundryOperations) {
			return new ApplicationDeployer(cloudFoundryOperations);
		}

		@Bean
		ServicesDeployer services(CloudFoundryOperations cloudFoundryOperations) {
			return new ServicesDeployer(cloudFoundryOperations);
		}
	}
}

