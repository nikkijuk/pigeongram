package com.nikkijuk.pigeongram.domain.model


import java.util.Objects
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

    @field:JsonProperty("id", required = true) val id: kotlin.String,

    @field:JsonProperty("type", required = true) val type: kotlin.String,

    @field:JsonProperty("receivedDateTime", required = true) val receivedDateTime: java.time.OffsetDateTime,

    @field:JsonProperty("sentDateTime", required = true) val sentDateTime: java.time.OffsetDateTime,

    @field:JsonProperty("hasAttachments", required = true) val hasAttachments: kotlin.Boolean,

    @field:JsonProperty("internetMessageId", required = true) val internetMessageId: kotlin.String,

    @field:JsonProperty("subject", required = true) val subject: kotlin.String,

    @field:JsonProperty("body", required = true) val body: MessageContent,

    @field:JsonProperty("sender", required = true) val sender: EmailAddress,

    @field:JsonProperty("from", required = true) val from: EmailAddress,

    @field:JsonProperty("toRecipients", required = true) val toRecipients: kotlin.collections.List<EmailAddress>,

    @field:JsonProperty("ccRecipients") val ccRecipients: kotlin.collections.List<EmailAddress>? = null,

    @field:JsonProperty("bccRecipients") val bccRecipients: kotlin.collections.List<EmailAddress>? = null
) {

}
