package com.nikkijuk.pigeongram.domain.model


import org.springframework.data.mongodb.core.mapping.Document
import java.time.OffsetDateTime

/**
 * @Document annotation is only needed on root object
 */
@Document
data class EmailMessage(
    val id: String,
    val type: String,
    val receivedDateTime: OffsetDateTime,
    val sentDateTime: OffsetDateTime,
    val hasAttachments: Boolean,
    val internetMessageId: String,
    val subject: String,
    val body: MessageContent,
    val from: EmailAddress,
    val toRecipients: List<EmailAddress>,
    val ccRecipients: List<EmailAddress>? = null,
    val bccRecipients: List<EmailAddress>? = null
)
