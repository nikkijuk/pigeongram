package com.nikkijuk.pigeongram.domain

import com.azure.spring.data.cosmos.core.mapping.Container
import com.azure.spring.data.cosmos.core.mapping.PartitionKey
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Address is embedded within User and is normal POJO
 *
 * Kotlins data classes didn't work here as Jackson didn't manage to live with immutable stuff
 * Lateinit is used as all attributes are mandatory and thus non nullable
 */
data class Address(

    @field:JsonProperty("street", required = true) val street: kotlin.String,

    @field:JsonProperty("postalcode", required = true) val postalcode: kotlin.String,

    @field:JsonProperty("city", required = true) val city: kotlin.String
) {

}