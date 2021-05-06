package com.nikkijuk.pigeongram.domain.model

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * 
 * @param contentType 
 * @param content 
 */
data class MessageContent(

    @field:JsonProperty("contentType", required = true) val contentType: String,

    @field:JsonProperty("content", required = true) val content: String
)

