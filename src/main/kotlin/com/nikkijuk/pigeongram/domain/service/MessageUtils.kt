package com.nikkijuk.pigeongram.domain.service

import com.nikkijuk.pigeongram.domain.model.EmailMessage
import jakarta.mail.Folder
import jakarta.mail.MessagingException
import jakarta.mail.NoSuchProviderException
import jakarta.mail.Session
import jakarta.mail.Store
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
        } as Properties
    }

    fun fetchMessagesFromFolder (server: Properties, folder : String, userName: String, password : String): List<EmailMessage> {

        val protocol = server.getProperty("protocol")
        val session = Session.getDefaultInstance(server)

        var store: Store? = null
        var mailFolder : Folder? = null;
        return try {
            store = session.getStore(protocol)
            store.connect(userName, password)

            mailFolder = store.getFolder(folder)
            mailFolder.open(Folder.READ_WRITE)

            mailFolder.messages.map { it.toEntity() }

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