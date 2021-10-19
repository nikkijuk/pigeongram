# pigeongram

Simple backend mail service implemented with kotlin and friends.

## Content

Small example how to 
- describe build with Gradle Kotlin DSL
- define and generate api (dto's & controllers base api) with Open APi 3 and "kotlin-spring" generator
- persist information with document oriented database (first cosmos db, then mongo db) and using spring data repository 
- automate workflows with camunda bpm using in memory h2 database
- run everything within single spring boot application

## Prerequisites

- Kotlin
- Gradle
- Mondo db installed locally

## App

### Properties

TODO: Update to mongo db when implementation is done


Get keys from cosmos db accounts *keys* section and fill application.properties or set environment variables so that proper keys are used

#### application.properties

    TODO: Update to mongo db

    cosmos.uri=${ACCOUNT_HOST}
    cosmos.key=${ACCOUNT_KEY}
    cosmos.secondaryKey=${SECONDARY_ACCOUNT_KEY}

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

Tutorial about sping boot & kotlin

https://spring.io/guides/tutorials/spring-boot-kotlin/

open api generator

https://github.com/OpenAPITools/openapi-generator

kotlin-spring open api generator

https://github.com/OpenAPITools/openapi-generator/blob/master/docs/generators/kotlin-spring.md

