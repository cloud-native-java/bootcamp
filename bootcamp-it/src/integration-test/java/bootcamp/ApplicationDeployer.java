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
import java.util.HashMap;
import java.util.Map;

class ApplicationDeployer {

 private final CloudFoundryOperations cf;

 ApplicationDeployer(CloudFoundryOperations cf) {
  this.cf = cf;
 }

 Mono<Void> deployApplication(File jar, String applicationName,
  Map<String, String> envOg, Duration timeout, String... svcs) {
  return cf.applications().push(pushApp(jar, applicationName))// <1>
   .then(bindServices(applicationName, svcs)) // <2>
   .then(setEnvironmentVariables(applicationName, new HashMap<>(envOg)))// <3>
   .then(startApplication(applicationName, timeout));// <4>

 }

 private PushApplicationRequest pushApp(File jar, String applicationName) {
  return PushApplicationRequest.builder().name(applicationName).noStart(true)
   .randomRoute(true)
   .buildpack("https://github.com/cloudfoundry/java-buildpack.git")
   .application(jar.toPath()).instances(1).build();
 }

 private Mono<Void> bindServices(String applicationName, String[] svcs) {
  return Flux
   .just(svcs)
   .flatMap(
    svc -> {
     BindServiceInstanceRequest request = BindServiceInstanceRequest.builder()
      .applicationName(applicationName).serviceInstanceName(svc).build();
     return cf.services().bind(request);
    }).then();
 }

 private Mono<Void> startApplication(String applicationName, Duration timeout) {
  return cf.applications().start(
   StartApplicationRequest.builder().name(applicationName)
    .stagingTimeout(timeout).startupTimeout(timeout).build());
 }

 private Mono<Void> setEnvironmentVariables(String applicationName,
  Map<String, String> env) {
  return Flux
   .fromIterable(env.entrySet())
   .flatMap(
    kv -> cf.applications().setEnvironmentVariable(
     SetEnvironmentVariableApplicationRequest.builder().name(applicationName)
      .variableName(kv.getKey()).variableValue(kv.getValue()).build())).then();
 }
}
