package bootcamp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cloudfoundry.operations.CloudFoundryOperations;

//@formatter:off
import org.cloudfoundry.operations.applications
        .PushApplicationRequest;
import org.cloudfoundry.operations.applications
        .SetEnvironmentVariableApplicationRequest;
import org.cloudfoundry.operations.applications
        .StartApplicationRequest;
import org.cloudfoundry.operations.services
        .BindServiceInstanceRequest;
//@formatter:on

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.time.Duration;
import java.util.stream.Stream;

class ApplicationDeployer {

    private final CloudFoundryOperations cf;

    private final Log log = LogFactory.getLog(getClass());

    ApplicationDeployer(CloudFoundryOperations cf) {
        this.cf = cf;
    }

    Flux<Void> deployApplication(File jar, String applicationName, String... svcs) {
        log.info("deployApplication!");
        return cf
                .applications()
                .push(
                        PushApplicationRequest
                                .builder()
                                // <1>
                                .name(applicationName).noStart(true).randomRoute(true)
                                .buildpack("https://github.com/cloudfoundry/java-buildpack.git")
                                .application(jar.toPath()).instances(1).build())
                .flatMap(x ->
                        Flux.fromStream(Stream.of(svcs)) // <2>
                                .map(svc -> {
                                    BindServiceInstanceRequest request = BindServiceInstanceRequest.builder().applicationName(applicationName)
                                            .serviceInstanceName(svc).build();
                                    return cf.services().bind(request);
                                }))
                .flatMap(x ->
                        cf.applications().setEnvironmentVariable(
                                SetEnvironmentVariableApplicationRequest.builder()
                                        // <3>
                                        .name(applicationName).variableName("SPRING_PROFILES_ACTIVE")
                                        .variableValue("cloud").build()))
                .flatMap(x -> {
                    StartApplicationRequest request1 = StartApplicationRequest.builder()
                            // <4>
                            .name(applicationName).stagingTimeout(Duration.ofMinutes(5))
                            .startupTimeout(Duration.ofMinutes(5)).build();
                    return cf.applications().start(request1);
                });

    }
}
