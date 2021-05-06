package com.nikkijuk.pigeongram.domain.service

import com.nikkijuk.pigeongram.domain.model.EmailAddress
import com.nikkijuk.pigeongram.domain.model.EmailMessage
import com.nikkijuk.pigeongram.domain.model.MessageContent
import jakarta.mail.Address
import jakarta.mail.Message
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.*

// to entity
fun Message.toEntity () =
    EmailMessage (
        id = this.messageNumber.toString(),
        type = "imap",
        receivedDateTime = convertDate(this.receivedDate),
        sentDateTime = convertDate(this.sentDate),
        hasAttachments = false, // this.hasAttachments,
        internetMessageId = this.messageNumber.toString(),
        subject = this.subject,
        body = convertContent(this),
        from = convertAddresses(this.from).first(),
        toRecipients = convertAddresses(this.getRecipients(Message.RecipientType.TO)),
        ccRecipients = convertAddresses(this.getRecipients(Message.RecipientType.CC)),
        bccRecipients = convertAddresses(this.getRecipients(Message.RecipientType.BCC))
    )

private fun convertContent(message: Message) =
    MessageContent(contentType = message.contentType, content = message.content.toString())

private fun convertAddresses(addresses : Array<Address>?) : List<EmailAddress> {
    return addresses?.map { convertAddress(it)} ?: listOf()
}

private fun convertAddress(address : Address) =
    EmailAddress(address.toString(), address.toString())

private fun convertDate(dt:Date) =
    OffsetDateTime.of(
        LocalDateTime.ofInstant(
            dt.toInstant(),
            ZoneId.of("UTC")
        ), ZoneOffset.UTC
    )
