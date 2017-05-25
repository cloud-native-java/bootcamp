package bootcamp;

import org.cloudfoundry.operations.CloudFoundryOperations;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.time.Duration;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SpringConfigurationIT.Config.class)
public class SpringConfigurationIT {

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

        CountDownLatch countDownLatch = new CountDownLatch(1);
        servicesDeployer
                .deployService(applicationName, mysqlServiceName) // <1>
                .thenEmpty(i -> applicationDeployer.deployApplication(jar, applicationName, mysqlServiceName)) // <2>
                .thenEmpty(i -> {
                    try {
                        countDownLatch.countDown();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }); // <3>

        countDownLatch.await();

    }

    @SpringBootApplication
    public static class Config {

        @Bean
        ApplicationDeployer applications(CloudFoundryOperations cf) {
            return new ApplicationDeployer(cf);
        }

        @Bean
        ServicesDeployer services(CloudFoundryOperations cf) {
            return new ServicesDeployer(cf);
        }
    }

}
