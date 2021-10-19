package com.nikkijuk.pigeongram.web

import com.nikkijuk.pigeongram.PigeongramApplicationKt
import com.nikkijuk.pigeongram.domain.service.MessageUtils
import com.nikkijuk.pigeongram.generated.api.MessagesApi
import com.nikkijuk.pigeongram.generated.model.EmailMessage
import com.nikkijuk.pigeongram.persistence.MessageRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class MessagesController (
    val messageRepository: MessageRepository
) : MessagesApi {

    // for POP3
    //String protocol = "pop3";
    //String host = "outlook.office365.com";
    //String port = "995";

    // for IMAP
    val protocol = "imap"
    val host = "outlook.office365.com"
    val port = "993"

    val folder = "test"

    // TODO: Replace these with real ones
    val userName = "user@server.com"
    val password = "password"

    private val logger: Logger = LoggerFactory.getLogger(PigeongramApplicationKt::class.java)

    override fun findMessages(): ResponseEntity<List<EmailMessage>> {
        val foundMessages = messageRepository.findAll()
        logger.info("found users: $foundMessages")
        return ResponseEntity(foundMessages.map { it.toApi() }, HttpStatus.OK)
    }

    override fun syncMessages(): ResponseEntity<List<EmailMessage>> {
         // should go to service -- as most operations here which actually belong to domain
        val serverProps = MessageUtils.createServerProperties(protocol, host, port)
        val foundMessages = MessageUtils.fetchMessagesFromFolder(serverProps, folder, userName, password)
        foundMessages.forEach {
            val createdUser = messageRepository.save(it)
            logger.info("saved user: $createdUser")
        }

        return findMessages()
    }

}
