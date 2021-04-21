package com.nikkijuk.pigeongram

import com.nikkijuk.pigeongram.domain.Address
import com.nikkijuk.pigeongram.domain.User
import com.nikkijuk.pigeongram.persistence.ReactiveUserRepository
import com.nikkijuk.pigeongram.persistence.UserRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class PigeongramApplicationKt : CommandLineRunner {

    private val logger: Logger = LoggerFactory.getLogger(PigeongramApplicationKt::class.java)

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var reactiveUserRepository: ReactiveUserRepository

    override fun run(vararg var1: String?) {
        val address1 = Address("Waldstrasse 1", "04105", "Leipzig")
        val address2 = Address("Christianstrasse 2", "04105", "Leipzig")
        val address3 = Address("Lindenauer Markt 21", "04177", "Leipzig")

        val user1 = User("testId1", "testFirstName", "testLastName1", listOf(address1, address2))
        val user2 = User("testId2", "testFirstName", "testLastName2", listOf(address2, address3))
        logger.info("Using sync repository")

        // <Delete>
        userRepository.deleteAll()

        // </Delete>

        // <Create>
        logger.info("Saving user : {}", user1)
        userRepository.save(user1)

        // </Create>
        logger.info("Saving user : {}", user2)
        userRepository.save(user2)

        // to find by Id, please specify partition key value if collection is partitioned
        val result: User = userRepository.findByIdAndLastName(user1.id, user1.lastName)
        logger.info("Found user : {}", result)

        val usersIterator = userRepository.findByFirstName("testFirstName").iterator()
        logger.info("Users by firstName : testFirstName")
        while (usersIterator.hasNext()) {
            logger.info("user is : {}", usersIterator.next())
        }

        // find with postalcode

        val postalcode = "04177"
        val usersByPostalcode = userRepository.getUsersByPostalcode(postalcode).iterator()
        logger.info("Users by postalcode : {}",postalcode)
        while (usersByPostalcode.hasNext()) {
            logger.info("user living at {} is : {}", postalcode, usersByPostalcode.next())
        }

        // reactive

        logger.info("Using reactive repository")

        // <Query>
        val users = reactiveUserRepository.findByFirstName("testFirstName")
        users?.map { u ->
            logger.info("user is : {}", u)
            u
        }?.subscribe()

        // </Query>
    }

    /**
     * TODO: this is kinda ugly way - should not be needed
     */
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(PigeongramApplicationKt::class.java, *args)
        }
    }

}
