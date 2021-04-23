package com.nikkijuk.pigeongram.email

import java.util.*
import jakarta.mail.Flags
import jakarta.mail.Folder
import jakarta.mail.Message
import jakarta.mail.MessagingException
import jakarta.mail.NoSuchProviderException
import jakarta.mail.Session
import jakarta.mail.internet.MimeMessage

class ImapReader {

    fun renameMessage(folder: Folder, message: Message, subject: String) {
        try {
            val renamedMessage = MimeMessage(message as MimeMessage)

            renamedMessage.setSubject(subject)
            renamedMessage.setHeader("Thread-Topic", subject)
            renamedMessage.setFlags(message.flags ?: Flags(), true)

            renamedMessage.saveChanges()
            folder.appendMessages(arrayOf(renamedMessage))
        } catch (e: Exception) {
            println(e.message) //logger.error(e.message, e)
        } catch (t: Throwable) {
            println(t.message) //logger.error(t.message, t)
        } finally {
            //deleting original message
            message.setFlag(Flags.Flag.DELETED, true)
            folder.expunge()
        }
    }

    fun test () {

        // for POP3
//String protocol = "pop3";
//String host = "outlook.office365.com";
//String port = "995";

// for IMAP
        val protocol = "imap"
        val host = "outlook.office365.com"
        val port = "993"

//credentials
        val userName = "user@server.com"
        val password = "password"


        val properties: Properties = getServerProperties(protocol, host, port)
        val session = Session.getDefaultInstance(properties)

        try {
            // connects to the message store
            val store = session.getStore(protocol)
            store.connect(userName, password)

            // opens the inbox folder
            val folder = "test"
            val mailFolder = store.getFolder (folder)// ("INBOX")
            mailFolder.open(Folder.READ_WRITE)

            // fetches new messages from server
            val messages = mailFolder.messages

            // process and tag email
            for (message in messages){
                //Your code to process each email here
                renameMessage(mailFolder, message, "[PROCESSED] ${message.subject}")
            }

            // disconnect
            mailFolder.close(false)
            store.close()

        } catch (e: NoSuchProviderException) {
            println("No provider for protocol: $protocol") //logger.error(e.message, e)
        } catch (e: MessagingException) {
            println("Could not connect to the message store") //logger.error(e.message, e)
        }

    }

    fun getServerProperties(protocol: String, host: String, port: String): Properties {
        val properties = Properties()

        // server setting
        properties.put(String.format("mail.%s.host", protocol), host)
        properties.put(String.format("mail.%s.port", protocol), port)

        // SSL setting
        properties.setProperty(String.format("mail.%s.socketFactory.class", protocol), "javax.net.ssl.SSLSocketFactory")
        properties.setProperty(String.format("mail.%s.socketFactory.fallback", protocol), "false")
        properties.setProperty(String.format("mail.%s.socketFactory.port", protocol), port)
        return properties
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            // test here
            val reader = ImapReader ()
            reader.test()
        }
    }
}

