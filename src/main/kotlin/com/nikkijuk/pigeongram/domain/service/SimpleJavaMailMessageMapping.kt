package com.nikkijuk.pigeongram.domain.service

import com.nikkijuk.pigeongram.domain.model.EmailMessage
import com.nikkijuk.pigeongram.domain.model.EmailAddress
import com.nikkijuk.pigeongram.domain.model.MessageContent
import jakarta.mail.Address
import jakarta.mail.Message
import org.simplejavamail.api.email.Email
import org.simplejavamail.api.email.Recipient
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.*

// NOTE: This mapping is not in use - to use it one would need to have jakarta mail 1.X version
// we have currently 2.X, which is java 8 api and has changed package name

// to entity
fun Email.toEntity () =
    EmailMessage (
        id = this.id.toString(),
        type = "imap",
        receivedDateTime = convertDate(this.sentDate!!) ?: OffsetDateTime.now(),
        sentDateTime = convertDate(this.sentDate!!) ?: OffsetDateTime.now(),
        hasAttachments = !this.attachments.isEmpty(),
        internetMessageId = this.id.toString(),
        subject = this.subject!!,
        body = MessageContent (contentType = "html", content = this.htmlText.toString()),
        from = address(this.fromRecipient!!),
        toRecipients = addresses(this.recipients, Message.RecipientType.TO),
        ccRecipients = addresses(this.recipients, Message.RecipientType.CC),
        bccRecipients = addresses(this.recipients, Message.RecipientType.BCC)
        //addresses = this.addresses?.map { it.toApi() } ?: listOf())
    )

private fun addresses(recipients : List<Recipient>, type : Message.RecipientType) : List<EmailAddress> {
    val result =
        recipients
            .filter {it.type?.equals(type) ?: false}
            .map {address(it)}
    return result
}

private fun address(recipient : Recipient) =
    EmailAddress(recipient?.name ?: "", recipient?.address ?: "")

private fun Message.convertAddress(address : Address) =
    EmailAddress(address.toString(), address.toString())

private fun convertDate(dt:Date) =
    OffsetDateTime.of(
        LocalDateTime.ofInstant(
            dt.toInstant(),
            ZoneId.of("UTC")
        ), ZoneOffset.UTC
    )
