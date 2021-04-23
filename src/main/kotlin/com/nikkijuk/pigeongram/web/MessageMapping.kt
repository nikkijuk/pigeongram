package com.nikkijuk.pigeongram.domain

import com.nikkijuk.pigeongram.domain.model.EmailMessage


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
                body = this.body,
                sender = this.sender,
                from = this.from,
                toRecipients = this.toRecipients,
                ccRecipients = this.ccRecipients,
                bccRecipients = this.bccRecipients
                //addresses = this.addresses?.map { it.toApi() } ?: listOf())
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
                body = this.body,
                sender = this.sender,
                from = this.from,
                toRecipients = this.toRecipients,
                ccRecipients = this.ccRecipients,
                bccRecipients = this.bccRecipients
                //addresses = this.addresses?.map { it.toApi() } ?: listOf())
        )
