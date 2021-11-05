# pigeongram

Simple backend mail service implemented with kotlin, camunda and friends.

## Name of project

"Pigeon post is the use of homing pigeons to carry messages. Pigeons are effective as messengers due to their natural homing abilities. The pigeons are transported to a destination in cages, where they are attached with messages, then the pigeon naturally flies back to its home where the recipient could read the message." - https://en.wikipedia.org/wiki/Pigeon_post

## Usecase

Pigeongram uses remote systems to send drafts and archive sent messages

![pigeongram dependencies](../../blob/main/diagrams/pigeongram_send_concept.png)

Archive is idempotent, but sending draft can't be rolled back or compensated with separate operation, so it's important to notify user if archive can't be done even after several retries.

In microservices world where services might die at any moment it's important to understand that state is needed so that retries can be done even after longer delay. 

See attached retry and compensating transaction descriptions for deeper discussion of topic

- https://docs.microsoft.com/en-us/azure/architecture/patterns/retry
- https://docs.microsoft.com/en-us/azure/architecture/patterns/compensating-transaction

Here's excellent presentation how one could try to solve microservices integrartion and communication challenges with Workflow engine

- https://www.youtube.com/watch?v=7uvK4WInq6k

## Technologies

This example project tests how to

- describe build with Gradle Kotlin DSL
- define and generate rest api (dto's & controllers base api) with Open Api 3.X and "kotlin-spring" generator
- persist information with document oriented database (first cosmos db, then mongo db) using spring data repository 
- describe process using OMG BPMN 2.0 using Camunda Modeler
- automate workflows with camunda bpm using in memory h2 database
- run everything within single spring boot application (embedded process engine)

Open api 3 yaml format is used to describe user and messages api, "kotlin-spring" generator to generate base APIs and Api model (DTO) classes for spring boot rest controllers.

Mongo db is  used locally to allow direct usage of JSON objects without database schemas - if different type of persistence is needed sql server is later taken in use.

Camunda is used to implement "stateful retry". Datatabas backed process state allows easy implementation of retry and compensation actions.

H2 db is used to persist process instances to single file. File is stored within project and allows easy setup and testing.

## Major changes

Before Mongo Db Cosmos Db was used in project. Cosmos DB was ok and functioning. It was bit hard to cut through examples, which were partially outdated, but at the end solution was elegant. Reason to change to Mongo DB was that my free tier credentials for Cosmos DB run out after short time, so billing model of Cosmos DB didn't really suit for experiments.

## Workflow automation

Camunda has components for modelling processes, running and creating process instances, and controlling created processes and process instances.

![process engine](../../blob/main/diagrams/camunda_architecture_overview.png)

Use case is to automate backend process

- Embedded process engine and process implementation run in same scope (JVM/Service)
- Started process instances are persisted in database (one engine & database per microservice)
- Processes and rules are described with standard language (OMG: BPMN, DMN, CMMN)
- Application components have ports which use domain model (Ports & Adapters Pattern)
- Application components are integrated to process with thin adapters (Service task / Java API)

Main reason to use workflow engine is in this PoX tactical, it should help programmer within single microservice to implement "stateful retry" in elegant and simply way. 

Bernd Rücker says it so

- Using a lightweight workflow engine allows you to handle stateful patterns without investing a lot of effort or risking accidental complexity by applying homegrown solutions.

https://blog.bernd-ruecker.com/fail-fast-is-not-enough-84645d6864d3

Prototypes architectural overview looks like this

![send process](../../blob/main/diagrams/workflow_programming_model.png)

Only service tasks in use, only sync calls from process to business logic, so this is pretty simple.

Prototype process implemented looks like this

![send process](../../blob/main/diagrams/send_draft_process.png)

Process overview

Send draft process could contain steps like

- Validate draft (error if draft is not complete)
- Move draft to outbox 
- Send (3 retries, regular interval, after that error)
- Move draft to sent
- Archive  (4 retries, increasing delay, after that error)

in case of Error: notify user

Orchestrating backend logic using adapters which implement process steps supports loose coupling of components and makes each step of process easily testable.

Workflow engine can be used in different roles. 

![workflow engine roles](../../blob/main/diagrams/workflow_engine_roles.jpg)

Even if it's in this PoC used to help "Point-to-point communication by request/response" there is features for other usecases also if needed and sync and async communication can be combined flexibly.

https://camunda.com/blog/2020/02/the-microservices-workflow-automation-cheat-sheet-the-role-of-the-workflow-engine/

## Workflow modelling

Process is modelled using business model notation BPMN 2.0. Also decision modelling notation DMN 1.3 and case management modelling notation CMMN 1.1 are supported by Camunda but not used in this prototype,

During BPMN modelling Camunda Modeler plugins can help to validate model completeness and correctness. BPMN Linter plugin, Technical property info plugin,Tooltip plugin and Transaction Boundaries plugin seemed to be useful.

linting

- https://github.com/camunda/camunda-modeler-linter-plugin

tooltipa 

- https://github.com/viadee/camunda-modeler-tooltip-plugin

technical property info

- https://github.com/umb/camunda-modeler-property-info-plugin

transaction boundaries

- https://github.com/bpmn-io/camunda-transaction-boundaries

These and some other plugins and installation of them is described here 

- https://emsbach.medium.com/the-best-free-plugins-for-camundas-bpmn-2-modeler-14eee0c9fdd2

## Wokflow testing

Fancy, but can I test it?

BPMN models are testable in several ways

- Black Box: Start spring boot test app and run process within it with real java delegates and mocked dependencies -> this is like one would bring dinosaur to park just to see what happens -> tests are coarse grained, there is typically only small amount of complex tests implemented
- White Box: just create java delegates (optionally mock them), create process instance, fill process instance with right data,  run each step of process and see what happens -> tests are fine grained, there's lot of small simple tests, they are easy to change and create

Documentation shows this example for testing, but there's variability on which tools to use for mocking 

![send process](../../blob/main/diagrams/process_test_scopes.png)

Documentation describes tests scopes following way

- Scope 1: Local, functional correctness of the process model with data, conditions and delegation code, usually implemented as a unit test. 
- Scope 2: Integration with business logic inside the runtime container, for Java EE applications usually implemented as an Arquillian-based integration test.
- Scope 3: Integration with external systems and UI.

https://camunda.com/best-practices/testing-process-definitions/

## Api generation

Rest api is described at specs/api.yaml with open api 3.0 yaml. JSON would have been also supported, but yaml is compact and allows multiline documentation. 

Gradle is configured to use kotlin generator and to generate models and apis. It would be possible to generate java, but having same language on generated and handwritten code might be advantage.

It's important to note that in some use cases it's desirable to generate as much as possible, but for application development it might be that controllers that contain program flow logic and coordinate how business logic is called might come out too restrictive if generation is used too much.

```
// see supported options for kotlin-spring here
// https://openapi-generator.tech/docs/generators/kotlin-spring/
openApiGenerate {
    generatorName.set("kotlin-spring")
    inputSpec.set("$rootDir/specs/api.yaml".toString())
    outputDir.set("$buildDir/generated".toString())
    modelPackage.set("com.nikkijuk.pigeongram.generated.model")
    apiPackage.set("com.nikkijuk.pigeongram.generated.api")
    //invokerPackage.set("com.nikkijuk.pigeongram.generated.invoker")
    configOptions.set(
        mapOf(
            // it's important to generate only interfaces
            // otherwise one needs to start from service interface
            "interfaceOnly" to "true",

            // one can generate service api also
            // this is especially useful if one generated complete controller implementation
            //"serviceInterface" to "true",

            // there's quite some generation options - just play with them to see what happens
            //"delegatePattern" to "true",

            "useBeanValidation" to "false",
            "enumPropertyNaming" to "UPPERCASE",
            "dateLibrary" to "java8"
        )
    )
}
```

## Storage

These installation instructions are for OSX / M1

Install latest version of mongo db
- brew tap mongodb/brew
- brew install mongodb-community

Start mongodb
- mongod --config /opt/homebrew/etc/mongod.conf --fork

Stop mongodb
- connect to the mongod from the mongo shell, and issue the shutdown command as needed

Troubleshooting
- less /opt/homebrew/var/log/mongodb/mongo.log

## Workflow engine

Camunda seed implementation is generated with https://start.camunda.com/

Camunda seed implementation is using Maven as build tool, but it's easy to take needed parts to Gradle. Important is that Spring Boot and Camunda versions need to match each other.

Most important dependencies at gradle build script are spring starters which start workflow engine, rest api and tooling.

```
    implementation("org.camunda.bpm.springboot:camunda-bpm-spring-boot-starter-rest") 

    implementation("org.camunda.bpm.springboot:camunda-bpm-spring-boot-starter-webapp") 
```
    
## App

#### Camunda

Camunda starts automatically with app and can be called at localhost:8080 using demo/demo

Camunda data source and admin user are defined at resources/application.yaml

```
spring.datasource.url: jdbc:h2:file:./camunda-h2-database

camunda.bpm.admin-user:
id: demo
password: demo
```

#### Mongo db

mongo db needs to be started manually before starting pigeongram

When default settings are used no application config is needed

```
# mongodb
# -------
spring.data.mongodb.host=localhost
spring.data.mongodb.port=27017
spring.data.mongodb.database=testdb
```

### Role of application

- Application loads some test data to document database
- Please see PigeongramApplicationKt

### Role of controller

- Controller takes requests in
- Converts request parameters from api model to entities if needed
- Executes database operations using injected spring data cosmosdb repository
- Converts responses from entities to api model if needed
- Returns response

### Code for controller

```
  @RestController
  class SampleAppController @Autowired constructor(
  val userRepository: UserRepository
  ) : UsersApi {

      private val logger: Logger = LoggerFactory.getLogger(SampleApplication::class.java)
  
      override fun createUser(@RequestBody user: User): ResponseEntity<User> {
          val user = userRepository.save(user.toEntity())
          logger.info("saved user: $user")
          return ResponseEntity(user.toApi(), HttpStatus.OK)
      }
  
      override fun findUsers(): ResponseEntity<List<User>> {
          val users = userRepository.findAll()
          logger.info("found users: $users")
          return ResponseEntity(users.map { it.toApi() }, HttpStatus.OK)
      }
  
      override fun getUserById(@PathVariable("id") id: kotlin.String
      ): ResponseEntity<User> {
          val user = userRepository.findById(id)
          logger.info("found with id '$id' user: $user")
          return user
                  .map { ResponseEntity(it.toApi(), HttpStatus.OK) }
                  .orElse(ResponseEntity(HttpStatus.NOT_FOUND))
      }
  }
```

### Role of process step

- Process steps implement JavaDelegate
- override execute (..) method
- take DelegateExecution as parameter

So; they implement simple command pattern, which is neat and very old school

https://wiki.c2.com/?CommandPattern

Binding process step to process

- Select service task at Camunda modeler
- Select "DelegateExpression" as implementation
- set Delegate expression to "#{processStepName}" so that it matches to bean annotated with @Named

[Best practices for implementing JavaDelegate](https://camunda.com/best-practices/invoking-services-from-the-process/#_understanding_and_using_strong_java_delegates_strong) should be followed to enable reuse of java delegates and separation of business logic from process logic.

### Code for process step

Here is run process helper method and implementation of dummy process step which doesn't do anything useful but can be used for testing and prototyping.

NoOpDelegate can be bound to service task with delegate expression "#{noOpDelegate}".

```
private val log = KotlinLogging.logger { }

/*
* Simple around function which could also contain some logging, exception handling, etc.
 */
fun runProcess (ctx: DelegateExecution, action: DelegateExecution.() -> Unit) {
    try {
        ctx.action()
        log.info { "executed: ${ctx.processInstanceId} : ${ctx.currentActivityId}" }
    } catch (e: Exception) {
        log.error (e) { "failed to execute: ${ctx.processInstanceId} : ${ctx.currentActivityId}" }
        throw e
    }
}
    
/**
* Simple delegate which can be used to set up processes without functionality
*/
@Named
class NoOpDelegate : JavaDelegate {
    override fun execute(execution: DelegateExecution) {
        runProcess(execution)  {
            // do nothing
        }
    }
}
```

runProcess { add code here } is simple around function, which gives nice tracking log for execution.

runProcess or similar function can be used in all process steps to give them uniform structure.

### First process step at example process

First process step can fail with following result

- process doesn't start
- process continues using failure boundary event

![validate draft error](../../blob/main/diagrams/validate_draft_error_event.png)

Process needs 2 start parameters

- archive: should last step of process execute or not
- draftId: id of message to be sent

These parameters can be checked

- if step fails with IllegalArgumentException because of assertion error process doesn't start. 
- if step fails with BpmError failure handling boundary event will be executed and notify user task is activated. errorCode and errorMessage fields are filled with information from BpmError.

If first process step "validate draft" is executed successfully process continues on default execution path.

```
@Named
class ValidateDraftDelegate : JavaDelegate1 {

    private val log = KotlinLogging.logger { }

    override fun execute(execution: DelegateExecution) {

        runProcess(execution) {

            // exception here prevents starting process
            val archive = getVariable("archive")
            Asserts.notNull(archive, "archive")

            // exception here prevents starting process
            val draftId = getVariable("draftId")
            Asserts.notNull(draftId, "draftId")

            // exception here is directed to errror event
            if ((draftId as String).length < 2) {
                throw BpmnError ("INVALID_DRAFT_ID", "draftId is invalid")
            }

            println ( "validated draft '$draftId' with archival set to '$archive'" )
        }
    }
}
```

It's important to understand save points. Here unhandled IllegalArgumentException rolls back starting of process as there's no transaction boundary between start event and validation step. Transaction boundaries can be defined in model by defining "async before" or "async after".

Documentation says it so

- It is important to understand that every non-handled, propagated exception happening during process execution rolls back the current technical transaction.

https://camunda.com/best-practices/dealing-with-problems-and-exceptions/#__strong_dealing_strong_with_exceptions

and

- you can mimic a BPMN error in your Java code by explicitly throwing an exception of type org.camunda.bpm.engine.delegate.BpmnError. The consequences for the process is the same as if it were an explicit error end event.

https://camunda.com/best-practices/dealing-with-problems-and-exceptions/#_throwin‚g_and_handling_strong_bpmn_errors_strong

### send and fail only after 3 retries

Service tesks are normally executed synchronously. In this case task itself and engine run in same database transaction and if it's rolled back step fails.

When performing tasks asynchronously engine needs to have save point before running java delegates execute method. In this case process engine and task itself don't use same database transaction, and thus failure in task doesn't roll back step execution.

Setting save point before execution allows usage of retry strategies. Here "R3/PT1M" means Retry 3 times, Period of Time 1 Min. 

- asynchronous before: true
- retry time cycle: R3/PT1M

Period are defined as ISO 8601. See here how to use different types of retrx intervals, for example "PT10M,PT17M,PT20M".

https://docs.camunda.org/manual/7.10/user-guide/process-engine/the-job-executor/#retry-intervals

![async execution and retry](../../blob/main/diagrams/sending_retry.png)

It's important to understand what would have happened without save point. Failure would have rolled back whole process until exception as everything would have run synchronously in single database transaction.

as documentation says

- It is important to understand that every non-handled, propagated exception happening during process execution rolls back the current technical transaction. Therefore the process instance will find its last known wait state (or save point).

Discussion of differences between business exceptions and technical exceptions

https://docs.camunda.io/docs/reference/bpmn-processes/error-events/error-events/

Example of having several steps in one technical transaction

![rollback](../../blob/main/diagrams/rollback.png)

https://camunda.com/best-practices/dealing-with-problems-and-exceptions/#rolling-back-a-transaction

### Modelling retries and failure handling

When modelling retries and failures it's important to know how process engine works and

- use savepoints / transaction boundaries correctly (possibly setting "async before" to all tasks)
- not to model retry operations explicitly with BPMN

https://camunda.com/best-practices/operating-camunda/#modeling-for-easier-operations

### technical failure to process failure

Technical exceptions are retried by default 3 times. This can be configured in several ways.

https://docs.camunda.org/manual/7.12/user-guide/process-engine/the-job-executor/#retry-time-cycle-configuration

To be able to prevent process from stopping after 3 retries someone (any volunteers??) needs to convert technical exception to business error. 

Unfortunately the bridge which fills gap between your code which fails due to technical exception and process that is using business errors doesn't seem to be part of BPMN standard but neither Camunda as product. 

What seems to work
 
- throw technical exceptions until last retry is reached
- during last retry thow business process error

```
@Named
class SendMessageDelegate : JavaDelegate {

    private val log = KotlinLogging.logger { }

    override fun execute(execution: DelegateExecution) {

        runProcess(execution) {

            val draftId = getVariable("draftId")

            // exception here is directed to errror event after 3 retries
            if ((draftId as String).contains("FAIL")) {
                log.error { "failure sending $draftId" }

                // BPM error won't be retried - this is process stuu
                ifLastRetryThrowBpmnError("SENDING_FAILED", "sending  draft failed")

                // Technical errors will be retried
                throw RuntimeException ("sending draft failed")
            }
        }
    }

}
```

Tricky part is to know that logic is running within last retry possible, and for this we have implemented simple helper method.

To make it easy to read from code how program flow is implemented we use extra method which throws process error in case it's called during last retry. 


```
// please see source of code for discussion / context:
// https://forum.camunda.org/t/retry-task-for-error-handling/12476
fun ifLastRetryThrowBpmnError(errorCode: String, message: String) {
    log.info { "Checking if it's last retry" }
    if (isLastRetry()) {
        throw BpmnError(errorCode, message)
    }
}

fun isLastRetry(): Boolean {
    val jobExecutorContext = Context.getJobExecutorContext()
    if (jobExecutorContext?.currentJob != null) {
        val noOfRetries: Int = jobExecutorContext.currentJob.getRetries()
        val activityId = jobExecutorContext.currentJob.getActivityId()

        log.info("$activityId has ${noOfRetries - 1} retries remaining")

        return noOfRetries <= 1
    }
    return false
}
```

It would be possible to extend around method to do exactly what we did here. This might be more "framework" like solution, but would also hide flow control deeper.

### decision gateway for archival

Archiving process step is on default path. Archiving process step won't be reached if condition of not arhiving is selected, otherwise message is archived.

- condition type: Expression
- expression: "#{archive == "no"}"

![archival gateway](../../blob/main/diagrams/gateway_archival.png)

## Test it

### process tests with junit 5

To make sure process works test it in your IDE

Just run junit test class, which
- starts process instance at defined point
- runs job (service task) to get further on process
- checks that process is finished and has right state

```
@ExtendWith(ProcessEngineExtension::class)
class SendDraftProcessTestCaseAutomocking {

  @BeforeEach
  fun setup() {
    // automocking allows running process without delegate classes
    autoMock("send_draft.bpmn");
  }

  @AfterEach
  fun teardown() {
    Mocks.reset()
  }

  @Test
  @Deployment(resources = ["send_draft.bpmn"])
  fun shouldExecuteProcess() {

    // GIVEN

    // we create a new process instance
    val processInstance: ProcessInstance = runtimeService()
      .createProcessInstanceByKey("SendDraftProcess")
      .setVariables(withVariables("archive", "yes", "draftId", "123"))
      .startBeforeActivity("ArchiveMessageActivity") // start with last step
      .execute()

    // process is active and only one instance running
    assertThat(processInstance).isActive
    Assertions.assertThat(BpmnAwareTests.processInstanceQuery().count()).isEqualTo(1)

    // WHEN

   // archive job exists and we execute it
   assertThat(job("ArchiveMessageActivity", processInstance)).isNotNull
   execute(job("ArchiveMessageActivity", processInstance))

    // THEN

    // process instance contains variables and instance process is finished
    assertThat(processInstance).hasVariables ("archive", "draftId")
    assertThat(processInstance).isEnded
  }
}
```

It is useful to understand some concepts used here, since tests can be greatly expanded even if currently there's just skeleton for simple test

general testing concept 

-  https://camunda.com/best-practices/testing-process-definitions/

bpmn assertions
 
- https://github.com/camunda/camunda-bpm-assert/blob/master/docs/User_Guide_BPMN.md

mocking

- https://github.com/camunda-community-hub/camunda-bpm-mockito

### test Running App with Camunda Cockpit

TODO:

### test Running App with Rest

TODO:

Please see

- https://camunda.com/blog/2021/10/start-and-step-through-a-process-with-rest-feat-swaggerui/

### test locally

Start app

- ./gradlew clean build bootRun

Get existing users as list

- http://127.0.0.1:8080/users/

Get single user

- http://127.0.0.1:8080/users/testId1

Add user

- get json returned by single user
- change id
- post to http://127.0.0.1:8080/users/ with json body

Get added user

- get as single used with new id

## Resources

Mongo db community installation to osx

https://docs.mongodb.com/manual/tutorial/install-mongodb-on-os-x/

https://github.com/mongodb/homebrew-brew

Tutorial about sping boot & kotlin

https://spring.io/guides/tutorials/spring-boot-kotlin/

open api generator

https://github.com/OpenAPITools/openapi-generator

kotlin-spring open api generator

https://github.com/OpenAPITools/openapi-generator/blob/master/docs/generators/kotlin-spring.md

open api generator gradle plugin

https://plugins.gradle.org/plugin/org.openapi.generator

Spring data document docs

https://docs.spring.io/spring-data/data-document/docs/current/reference/html/

Spring data mongo db docs

https://docs.spring.io/spring-data/mongodb/docs/current/reference/html/#reference

Camunda sping boot integration docs

https://docs.camunda.org/get-started/spring-boot/
