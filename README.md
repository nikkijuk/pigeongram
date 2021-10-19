# pigeongram

Simple backend mail service implemented with kotlin and friends.

## Content

Small example how to 
- describe build with Gradle Kotlin DSL
- define and generate api (dto's & controllers base api) with Open Api 3.X and "kotlin-spring" generator
- persist information with document oriented database (first cosmos db, then mongo db) using spring data repository 
- automate workflows with camunda bpm using in memory h2 database
- run everything within single spring boot application

It is possible to convert this example to use Mongo DB by simply using spring-data-mongo-db. Most notable change: Methods using Query annotations on repositories need to be removed.

Open api 3 yaml format is used to describe user and messages api, "kotlin-spring" generator to generate base APIs and Api model (DTO) classes for spring boot rest controllers.

Local mongo db is  used to allow direct usage of JSON objects without database schemas - if different type of persistence is needed sql server is later taken in use.


## Prerequisites

- Kotlin
- Gradle
- Mondo db installed locally

NOTE: There's no configuration needed when Mongo Db is installed locally. Otherwise application.properties needs to be adjusted.

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

## App

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


