# pigeongram

Simple backend mail service implemented with kotlin and friends.

## Content

Small example how to 
- describe build with Gradle Kotlin DSL
- define and generate api (dto's & controllers base api) with Open Api 3.X and "kotlin-spring" generator
- persist information with document oriented database (first cosmos db, then mongo db) using spring data repository 
- automate workflows with camunda bpm using in memory h2 database
- run everything within single spring boot application

Open api 3 yaml format is used to describe user and messages api, "kotlin-spring" generator to generate base APIs and Api model (DTO) classes for spring boot rest controllers.

Mongo db is  used locally to allow direct usage of JSON objects without database schemas - if different type of persistence is needed sql server is later taken in use.

H2 db is used to persist process instances to single file. File is stored within project and allows easy setup and testing.

## Major changes

Before Mongo Db Cosmos Db was used in project. Cosmos DB was ok and functioning. It was bit hard to cut through examples, which were partially outdated, but at the end solution was elegant. Reason to change to Mongo DB was that my free tier credentials for Cosmos DB run out after short time, so billing model of Cosmos DB didn't really suit for experiments.

## Process automation

Camunda Engine (Open source) has components for modelling processes, running and creating process instances, and controlling created processes and process instances.

![process engine](../../blob/main/diagrams/camunda_architecture_overview.png)

Use case is to automate backend process

- Embedded process engine and process implementation run in same scope (JVM/Service)
- Started process instances are persisted in database (one engine & database per microservice)
- Processes and rules are described with standard language (OMG: BPMN, DMN, CMMN)
- Application components have ports which use domain model (Ports & Adapters Pattern)
- Application components are integrated to process with thin adapters (Service task / Java API)

Prototype process implemented looks like this

![send process](../../blob/main/diagrams/send_draft_process.png)

Process overview

Send draft process could contain steps like

-    Validate draft (error if draft is not complete)
-    Move draft to outbox 
-    Send (3 retries, after that error)
-    Move draft to sent
-    Archive  

Orchestrating backend logic using adapters which implement process steps supports loose coupling of components and makes each step of process easily testable.

## Api generation

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

## App

#### Camunda

- Camunda starts automatically with app and caln be called at localhost:8080 using demo/demo

#### Mongo db

    # mongodb
    # -------
    spring.data.mongodb.host=localhost
    spring.data.mongodb.port=27017
    spring.data.mongodb.database=testdb

### Role of application

- Application loads some test data to document database

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

Here is run process helper method and implementation of dummy process step which doesn't do anything useful but can be used for testing.

- NoOpDelegate can be bound to service task with delegate expression "#{noOpDelegate}" 

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
    
fun DelegateExecution.logVariables () {
    variables.entries.iterator().forEach {
        log.info { "${it.key} = ${it.value}" }
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
It's important to understand save points. Here unhandled IllegalArgumentException rolls back starting of process.

Documentation says it so

- It is important to understand that every non-handled, propagated exception happening during process execution rolls back the current technical transaction.

https://camunda.com/best-practices/dealing-with-problems-and-exceptions/#__strong_dealing_strong_with_exceptions

and

- you can mimic a BPMN error in your Java code by explicitly throwing an exception of type org.camunda.bpm.engine.delegate.BpmnError. The consequences for the process is the same as if it were an explicit error end event.

https://camunda.com/best-practices/dealing-with-problems-and-exceptions/#_throwin‚g_and_handling_strong_bpmn_errors_strong

### send and fail only after 3 retries

Service tesk are normally executed synchronously. In this case task itself and engine run in same database transaction and if it's rolled back step fails.

When performing tasks asynchronously engine needs to have save point before running java delegates execute method. In this case process engine and task itself don't use same database transaction, and thus failure in task doesn't roll back step execution.

Setting save point before execution allows usage of retry strategies. Here "R3/PT1M" means Retry 3 times, Period of Time 1 Min. 

- asynchronous before: true
- retry time cycle: R3/PT1M

Period are defined as ISO 8601

![async execution and retry](../../blob/main/diagrams/retry_strategies.png)

It's important to understand what would have happened without save point. Failure would have rolled back whole process until exception as everything would have run synchromously in single database transation.

![rollback](../../blob/main/diagrams/rollback.png)

as documentation says

- It is important to understand that every non-handled, propagated exception happening during process execution rolls back the current technical transaction. Therefore the process instance will find its last known wait state (or save point).

https://camunda.com/best-practices/dealing-with-problems-and-exceptions/#rolling-back-a-transaction

### decision gateway for archival

Archiving process step is on default path. Archiving process step will be never reached if condition of not arhiving is reached, otherwise message is archived.

- condition type: Expression
- expression: "#{archive == "no"}"

![archival gateway](../../blob/main/diagrams/gateway_archival.png)

### Test it

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
