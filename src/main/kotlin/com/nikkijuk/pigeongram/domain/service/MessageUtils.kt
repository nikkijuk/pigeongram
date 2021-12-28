package com.nikkijuk.pigeongram.domain.service

import com.nikkijuk.pigeongram.domain.model.EmailMessage
import javax.mail.Folder
import javax.mail.MessagingException
import javax.mail.NoSuchProviderException
import javax.mail.Session
import javax.mail.Store
import java.util.*

object MessageUtils {

    fun createServerProperties(protocol: String, host: String, port: String): Properties {
        return with(Properties()) {
            // protocol
            setProperty ("protocol", protocol)

            // server setting
            setProperty ("mail.$protocol.host", host)
            setProperty ("mail.$protocol.port", port)

            // SSL setting
            setProperty ("mail.$protocol.socketFactory.class", "javax.net.ssl.SSLSocketFactory")
            setProperty ("mail.$protocol.socketFactory.fallback", "false")
            setProperty ("mail.$protocol.socketFactory.port", port)
            this
        }
    }

    fun fetchMessagesFromFolder (server: Properties, folder : String, userName: String, password : String): List<EmailMessage> {

        val protocol = server.getProperty("protocol")
        val session = Session.getDefaultInstance(server)

        var store: Store? = null
        var mailFolder : Folder? = null
        return try {
            store = session.getStore(protocol)
            store.connect(userName, password)

            mailFolder = store.getFolder(folder)
            mailFolder.open(Folder.READ_WRITE)

            mailFolder.messages
                // Simple java mail could help on parsing message content
                // but it's still on old java mail api
                // https://github.com/bbottema/simple-java-mail/issues/295
                //.map { it as MimeMessage }
                //.map { EmailConverter.mimeMessageToEmail(it)}
                .map { it.toEntity() }

        } catch (e: NoSuchProviderException) {
            println("No provider for protocol: $protocol")
            listOf<EmailMessage>()
        } catch (e: MessagingException) {
            println("Could not connect to the message store")
            listOf<EmailMessage>()
        } finally {
            mailFolder?.close(false)
            store?.close()
        }
    }
}