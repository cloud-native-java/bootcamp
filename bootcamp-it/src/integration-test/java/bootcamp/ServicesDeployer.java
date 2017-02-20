package bootcamp;

import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.services.CreateServiceInstanceRequest;
import org.cloudfoundry.operations.services.DeleteServiceInstanceRequest;
import org.cloudfoundry.operations.services.UnbindServiceInstanceRequest;
import reactor.core.publisher.Mono;

class ServicesDeployer {

 private final CloudFoundryOperations cf;

 ServicesDeployer(CloudFoundryOperations cf) {
  this.cf = cf;
 }

 Mono<Void> deployService(String applicationName, String mysqlServiceName) {
  return cf
   .services()
   .listInstances()
   .filter(si -> si.getName().equalsIgnoreCase(mysqlServiceName))
   // <1>
   .singleOrEmpty()
   .then(
    serviceInstance -> cf
     .services()
     .unbind(
      UnbindServiceInstanceRequest.builder()
       // <2>
       .applicationName(applicationName).serviceInstanceName(mysqlServiceName)
       .build())
     .then(cf.services().deleteInstance(DeleteServiceInstanceRequest.builder() // <3>
      .name(serviceInstance.getName()).build())))
   .then(
    cf.services().createInstance(
     CreateServiceInstanceRequest.builder()
      // <4>
      .serviceName("p-mysql").planName("100mb")
      .serviceInstanceName(mysqlServiceName).build()));
 }
}
