package com.nikkijuk.pigeongram.domain.service

import com.nikkijuk.pigeongram.domain.model.EmailMessage
import com.nikkijuk.pigeongram.generated.model.CommunicationParty
import com.nikkijuk.pigeongram.generated.model.EmailAddress
import com.nikkijuk.pigeongram.generated.model.MessageContent
import jakarta.mail.Address
import jakarta.mail.Message
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.*



// to entity
fun jakarta.mail.Message.toEntity () =
    EmailMessage (
        id = this.messageNumber.toString(),
        type = "imap",
        receivedDateTime = convertDate(this.receivedDate),
        sentDateTime = convertDate(this.sentDate),
        hasAttachments = false, // this.hasAttachments,
        internetMessageId = this.messageNumber.toString(),
        subject = this.subject,
        body = MessageContent (contentType = this.contentType, content = this.content.toString()),
        sender = convertParty(this.from.get(0)),
        from = convertParty(this.from.get(0)),
        toRecipients = listOf (convertParty(this.allRecipients.get(0))),
        ccRecipients = listOf (convertParty(this.allRecipients.get(0))),
        bccRecipients = listOf (convertParty(this.allRecipients.get(0)))
        //addresses = this.addresses?.map { it.toApi() } ?: listOf())
    )

private fun Message.convertParty(address : Address) =
    CommunicationParty(emailAddress = EmailAddress(address.toString(), address.toString()))

private fun convertDate(dt:Date) =
    OffsetDateTime.of(
        LocalDateTime.ofInstant(
            dt.toInstant(),
            ZoneId.of("UTC")
        ), ZoneOffset.UTC
    )
