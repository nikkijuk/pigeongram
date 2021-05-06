package com.nikkijuk.pigeongram.domain.model


import com.fasterxml.jackson.annotation.JsonProperty

/**
 *
 * @param id
 * @param type
 * @param receivedDateTime
 * @param sentDateTime
 * @param hasAttachments
 * @param internetMessageId
 * @param subject
 * @param body
 * @param sender
 * @param from
 * @param toRecipients
 * @param ccRecipients
 * @param bccRecipients
 */
data class EmailMessage(

    @field:JsonProperty("id", required = true) val id: String,

    @field:JsonProperty("type", required = true) val type: String,

    @field:JsonProperty("receivedDateTime", required = true) val receivedDateTime: java.time.OffsetDateTime,

    @field:JsonProperty("sentDateTime", required = true) val sentDateTime: java.time.OffsetDateTime,

    @field:JsonProperty("hasAttachments", required = true) val hasAttachments: Boolean,

    @field:JsonProperty("internetMessageId", required = true) val internetMessageId: String,

    @field:JsonProperty("subject", required = true) val subject: String,

    @field:JsonProperty("body", required = true) val body: MessageContent,

    @field:JsonProperty("from", required = true) val from: EmailAddress,

    @field:JsonProperty("toRecipients", required = true) val toRecipients: List<EmailAddress>,

    @field:JsonProperty("ccRecipients") val ccRecipients: List<EmailAddress>? = null,

    @field:JsonProperty("bccRecipients") val bccRecipients: List<EmailAddress>? = null
)
