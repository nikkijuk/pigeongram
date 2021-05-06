package com.nikkijuk.pigeongram.web

import com.nikkijuk.pigeongram.PigeongramApplicationKt
import com.nikkijuk.pigeongram.generated.api.UsersApi
import com.nikkijuk.pigeongram.persistence.UserRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class UsersController @Autowired constructor(
        val userRepository: UserRepository
) : UsersApi {

    private val logger: Logger = LoggerFactory.getLogger(PigeongramApplicationKt::class.java)

    override fun createUser(@RequestBody user: com.nikkijuk.pigeongram.generated.model.User): ResponseEntity<com.nikkijuk.pigeongram.generated.model.User> {
        val createdUser = userRepository.save(user.toEntity())
        logger.info("saved user: $createdUser")
        return ResponseEntity(createdUser.toApi(), HttpStatus.OK)
    }

    override fun findUsers(): ResponseEntity<List<com.nikkijuk.pigeongram.generated.model.User>> {
        val foundUsers = userRepository.findAll()
        logger.info("found users: $foundUsers")
        return ResponseEntity(foundUsers.map { it.toApi() }, HttpStatus.OK)
    }

    override fun getUserById(@PathVariable("id") id: String
    ): ResponseEntity<com.nikkijuk.pigeongram.generated.model.User> {
        val foundUser = userRepository.findById(id)
        logger.info("found with id '$id' user: $foundUser")
        return foundUser
                .map { ResponseEntity(it.toApi(), HttpStatus.OK) }
                .orElse(ResponseEntity(HttpStatus.NOT_FOUND))
    }
}
