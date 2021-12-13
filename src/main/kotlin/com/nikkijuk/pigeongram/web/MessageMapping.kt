package com.nikkijuk.pigeongram.web

import com.nikkijuk.pigeongram.domain.model.EmailAddress as EmailAddressDomain
import com.nikkijuk.pigeongram.domain.model.EmailMessage as EmailMessageDomain
import com.nikkijuk.pigeongram.domain.model.MessageContent as MessageContentDomain
import com.nikkijuk.pigeongram.generated.model.EmailAddress as EmailAddressApi
import com.nikkijuk.pigeongram.generated.model.EmailMessage as EmailMessageApi
import com.nikkijuk.pigeongram.generated.model.MessageContent as MessageContentApi

// to api
fun EmailMessageDomain.toApi () =
        EmailMessageApi (
                id = this.id,
                type = this.type,
                receivedDateTime = this.receivedDateTime,
                sentDateTime = this.sentDateTime,
                hasAttachments = this.hasAttachments,
                internetMessageId = this.internetMessageId,
                subject = this.subject,
                body = this.body.toApi(),
                from = this.from.toApi(),
                toRecipients = this.toRecipients.map { it.toApi() },
                ccRecipients = this.ccRecipients?.map { it.toApi() } ?: listOf(),
                bccRecipients = this.bccRecipients?.map { it.toApi() } ?: listOf(),
        )

fun MessageContentDomain.toApi () =
        MessageContentApi (
                contentType = this.contentType,
                content = this.content,
        )

fun EmailAddressDomain.toApi () =
        EmailAddressApi (
                name = this.name,
                address = this.address,
        )

// to entity
fun EmailMessageApi.toDomain () =
        EmailMessageDomain (
                id = this.id,
                type = this.type,
                receivedDateTime = this.receivedDateTime,
                sentDateTime = this.sentDateTime,
                hasAttachments = this.hasAttachments,
                internetMessageId = this.internetMessageId,
                subject = this.subject,
                body = this.body.toDomain(),
                from = this.from.toDomain(),
                toRecipients = this.toRecipients.map { it.toDomain() },
                ccRecipients = this.ccRecipients?.map { it.toDomain() } ?: listOf(),
                bccRecipients = this.bccRecipients?.map { it.toDomain() } ?: listOf(),
        )

fun MessageContentApi.toDomain () =
        MessageContentDomain (
                contentType = this.contentType,
                content = this.content,
        )

fun EmailAddressApi.toDomain () =
        EmailAddressDomain (
                name = this.name,
                address = this.address,
        )
