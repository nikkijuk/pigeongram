package com.nikkijuk.pigeongram.domain.model

import com.azure.spring.data.cosmos.core.mapping.Container
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * User is root entity which is to be saved within cosmos db
 *
 * @Container annotation defines name of bucket where User objects are to be saved
 * @Container is only needed on root object
 */
@Container(containerName = "userContainer", ru = "400")
data class User(

    @field:JsonProperty("id", required = true) val id: kotlin.String,

    @field:JsonProperty("firstName", required = true) val firstName: kotlin.String,

    @field:JsonProperty("lastName", required = true) val lastName: kotlin.String,

    @field:JsonProperty("addresses") val addresses: kotlin.collections.List<Address>? = null
) {

}