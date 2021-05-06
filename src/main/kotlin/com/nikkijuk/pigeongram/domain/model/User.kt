package com.nikkijuk.pigeongram.domain.model

import com.azure.spring.data.cosmos.core.mapping.Container
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * User is root entity which is to be saved within cosmos db
 *
 * @Container annotation defines name of bucket where User objects are to be saved
 * @Container is only needed on root object
 */
@Container(containerName = "userContainer", ru = "100") // set limit to allow free azure usage
data class User(

    @field:JsonProperty("id", required = true) val id: String,

    @field:JsonProperty("firstName", required = true) val firstName: String,

    @field:JsonProperty("lastName", required = true) val lastName: String,

    @field:JsonProperty("addresses") val addresses: List<Address>? = null
)