package com.nikkijuk.pigeongram

import com.nikkijuk.pigeongram.domain.model.Address
import com.nikkijuk.pigeongram.domain.model.Signature
import com.nikkijuk.pigeongram.domain.model.User
import com.nikkijuk.pigeongram.persistence.SignatureRepository
import com.nikkijuk.pigeongram.persistence.UserRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
internal class LoadDatabase {
    @Bean
    fun initDatabase(
        signatureRepository: SignatureRepository,
        userRepository: UserRepository
    ): CommandLineRunner {
        return CommandLineRunner { args: Array<String?>? ->

            signatureRepository.deleteAll()

            signatureRepository.save(Signature(1, "jukka@nikki.com", "Greeting", "Hello World"))
            signatureRepository.save(Signature(2, "andere@person.de", "Grüsse", "Hallo Welt"))
            signatureRepository.findAll().forEach { employee -> log.info("Preloaded $employee") }

            userRepository.deleteAll()

            val address1 = Address("Waldstrasse 1", "04105", "Leipzig")
            val address2 = Address("Christianstrasse 2", "04105", "Leipzig")
            val address3 = Address("Lindenauer Markt 21", "04177", "Leipzig")

            val user1 = User("testId1", "testFirstName", "testLastName1", listOf(address1, address2))
            val user2 = User("testId2", "testFirstName", "testLastName2", listOf(address2, address3))


            userRepository.save(user1)
            userRepository.save(user2)

            userRepository.findAll().forEach { user -> log.info("Preloaded $user") }

        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(LoadDatabase::class.java)
    }
}

@SpringBootApplication
class PigeongramApplicationKt : CommandLineRunner {

    private val logger: Logger = LoggerFactory.getLogger(PigeongramApplicationKt::class.java)

    override fun run(vararg var1: String?) {
        logger.info("Started pigeongram")
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
