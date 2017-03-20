package bootcamp;

import cnj.CloudFoundryService;
import org.cloudfoundry.operations.applications.ApplicationManifest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SpringConfigurationIT.Config.class)
public class SpringConfigurationIT {

	@Autowired
	private CloudFoundryService cloudFoundryService;

	private RestTemplate restTemplate = new RestTemplate();

	@Test
	public void deploy() throws Throwable {
		File projectFolder = new File(new File("."), "../spring-configuration");
		String mysqlServiceName = "bootcamp-customers-mysql";
		cloudFoundryService.createServiceIfMissing("p-mysql", "100mb", mysqlServiceName);
		File manifest = new File(projectFolder, "manifest.yml");
		Map<File, ApplicationManifest> manifestFrom = this.cloudFoundryService.applicationManifestFrom(manifest);
		manifestFrom.forEach((k, f) -> {
			this.cloudFoundryService.pushApplicationUsingManifest(manifest);
			String urlForCustomerRestUrl = this.cloudFoundryService.urlForApplication(f.getName());
			ResponseEntity<String> responseEntity = this.restTemplate.getForEntity(urlForCustomerRestUrl + "/customers", String.class);
			Assert.assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
			String body = responseEntity.getBody();
			Assert.assertTrue(body.contains("rj@cnj.com"));
		});
	}

	@SpringBootApplication
	public static class Config {
	}
}
