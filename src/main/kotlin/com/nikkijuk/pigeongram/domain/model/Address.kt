package com.nikkijuk.pigeongram.domain.model

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Address is embedded within User and is normal POJO
 */
data class Address(

    @field:JsonProperty("street", required = true) val street: String,

    @field:JsonProperty("postalcode", required = true) val postalcode: String,

    @field:JsonProperty("city", required = true) val city: String
)