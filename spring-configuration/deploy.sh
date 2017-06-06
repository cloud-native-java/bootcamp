
cf create-service p-mysql 100mb bootcamp-customers-mysql            <1>

cf push -p target/configuration.jar bootcamp-customers \
 --random-route --no-start                                          <2>

cf bind-service bootcamp-customers bootcamp-customers-mysql         <3>

cf start bootcamp-customers                                         <4>
