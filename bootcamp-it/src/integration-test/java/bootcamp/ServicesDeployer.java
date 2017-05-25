package bootcamp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.services.CreateServiceInstanceRequest;
import org.cloudfoundry.operations.services.DeleteServiceInstanceRequest;
import org.cloudfoundry.operations.services.ServiceInstanceSummary;
import org.cloudfoundry.operations.services.UnbindServiceInstanceRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

class ServicesDeployer {

    private final Log log = LogFactory.getLog(getClass());
    private final CloudFoundryOperations cf;

    ServicesDeployer(CloudFoundryOperations cf) {
        this.cf = cf;
    }

    Mono<Void> deployService(String applicationName, String mysqlServiceName) {
        log.info("inside deployService()");
        Flux<ServiceInstanceSummary> si = this.step1(mysqlServiceName)
                .log()
                .publish()
                .refCount(2);
        Flux<Void> voidFlux = Flux.merge(step2_a(applicationName, mysqlServiceName, si),
                step2_b(mysqlServiceName, si));
        return voidFlux
                .thenEmpty(step3(mysqlServiceName)) ;
    }

    private Flux<ServiceInstanceSummary> step1(String mysqlServiceName) {
        return cf.services()
                .listInstances()
                .filter(si -> si.getName().equalsIgnoreCase(mysqlServiceName));
    }

    private Mono<Void> step3(String mysqlServiceName) {
        return cf.services().createInstance(CreateServiceInstanceRequest
                .builder()
                .serviceName("p-mysql")
                .planName("100mb")
                .serviceInstanceName(mysqlServiceName)
                .build()) ;
    }

    private Flux<Void> step2_a(String applicationName, String mysqlServiceName, Flux<ServiceInstanceSummary> step1) {
        return step1
                .filter(si -> si.getApplications().contains(applicationName))
                .flatMap(si -> {
                    log.info("step2_a");
                    return cf
                            .services()
                            .unbind(UnbindServiceInstanceRequest
                                    .builder()
                                    .applicationName(applicationName)
                                    .serviceInstanceName(mysqlServiceName)
                                    .build());
                });
    }

    private Flux<Void> step2_b(String mysqlServiceName, Flux<ServiceInstanceSummary> step1) {
        return step1
                .log()
                .flatMap(si -> {
                    log.info("step2_b");
                    return cf.services()
                            .deleteInstance(
                                    DeleteServiceInstanceRequest
                                            .builder()
                                            .name(mysqlServiceName)
                                            .build());
                });
    }
}
