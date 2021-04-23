package com.nikkijuk.pigeongram.domain

import com.nikkijuk.pigeongram.domain.model.EmailAddress
import com.nikkijuk.pigeongram.domain.model.EmailMessage
import com.nikkijuk.pigeongram.domain.model.MessageContent


// to api
fun EmailMessage.toApi () =
        com.nikkijuk.pigeongram.generated.model.EmailMessage (
                id = this.id,
                type = this.type,
                receivedDateTime = this.receivedDateTime,
                sentDateTime = this.sentDateTime,
                hasAttachments = this.hasAttachments,
                internetMessageId = this.internetMessageId,
                subject = this.subject,
                body = this.body.toApi(),
                sender = this.sender.toApi(),
                from = this.from.toApi(),
                toRecipients = this.toRecipients.map { it.toApi() } ?: listOf(),
                ccRecipients = this.ccRecipients?.map { it.toApi() } ?: listOf(),
                bccRecipients = this.bccRecipients?.map { it.toApi() } ?: listOf()
        )

fun MessageContent.toApi () =
        com.nikkijuk.pigeongram.generated.model.MessageContent (
                contentType = this.contentType,
                content = this.content
        )

fun EmailAddress.toApi () =
        com.nikkijuk.pigeongram.generated.model.EmailAddress (
                name = this.name,
                address = this.address
        )

// to entity
fun com.nikkijuk.pigeongram.generated.model.EmailMessage.toEntity () =
        EmailMessage (
                id = this.id,
                type = this.type,
                receivedDateTime = this.receivedDateTime,
                sentDateTime = this.sentDateTime,
                hasAttachments = this.hasAttachments,
                internetMessageId = this.internetMessageId,
                subject = this.subject,
                body = this.body.toEntity(),
                sender = this.sender.toEntity(),
                from = this.from.toEntity(),
                toRecipients = this.toRecipients.map { it.toEntity() } ?: listOf(),
                ccRecipients = this.ccRecipients?.map { it.toEntity() } ?: listOf(),
                bccRecipients = this.bccRecipients?.map { it.toEntity() } ?: listOf()
        )

fun com.nikkijuk.pigeongram.generated.model.MessageContent.toEntity () =
        MessageContent (
                contentType = this.contentType,
                content = this.content
        )

fun com.nikkijuk.pigeongram.generated.model.EmailAddress.toEntity () =
        EmailAddress (
                name = this.name,
                address = this.address
        )
