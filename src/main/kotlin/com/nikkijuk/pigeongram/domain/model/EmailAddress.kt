package com.nikkijuk.pigeongram.domain.model

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * 
 * @param name 
 * @param address 
 */
data class EmailAddress(

    @field:JsonProperty("name", required = true) val name: String,

    @field:JsonProperty("address", required = true) val address: String
)

