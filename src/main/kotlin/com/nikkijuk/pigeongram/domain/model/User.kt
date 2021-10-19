package com.nikkijuk.pigeongram.domain.model

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.data.mongodb.core.mapping.Document

/**
 * User is root entity which is to be saved within cosmos db
 *
 * @Document annotation is only needed on root object
 */
@Document
data class User(

    @field:JsonProperty("id", required = true) val id: String,

    @field:JsonProperty("firstName", required = true) val firstName: String,

    @field:JsonProperty("lastName", required = true) val lastName: String,

    @field:JsonProperty("addresses") val addresses: List<Address>? = null
)