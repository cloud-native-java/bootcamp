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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

class ApplicationDeployer {

    private final CloudFoundryOperations cf;

    private final Log log = LogFactory.getLog(getClass());

    ApplicationDeployer(CloudFoundryOperations cf) {
        this.cf = cf;
    }

    Mono<Void> deployApplication(File jar, String applicationName, Map<String, String> envOg, Duration timeout, String... svcs) {
        log.info("deployApplication!");
        Map<String, String> env = new HashMap<>(envOg);
        return cf
                .applications()
                .push(
                        PushApplicationRequest
                                .builder()
                                // <1>
                                .name(applicationName).noStart(true).randomRoute(true)
                                .buildpack("https://github.com/cloudfoundry/java-buildpack.git")
                                .application(jar.toPath()).instances(1).build())
                .then(
                        Flux.just(svcs) // <2>
                                .flatMap(
                                        svc -> {
                                            BindServiceInstanceRequest request = BindServiceInstanceRequest
                                                    .builder().applicationName(applicationName).serviceInstanceName(svc)
                                                    .build();
                                            return cf.services().bind(request);
                                        })
                                .then()
                )
                .then(
                        Flux.fromIterable(env.entrySet())
                                .flatMap(kv -> // <3>
                                        cf.applications().setEnvironmentVariable(SetEnvironmentVariableApplicationRequest.builder().name(applicationName).variableName(kv.getKey()).variableValue(kv.getValue()).build()))
                                .then()
                )

                .then(
                        cf.applications().start(StartApplicationRequest.builder().name(applicationName).stagingTimeout(timeout).startupTimeout(timeout).build())
                );

    }
}
