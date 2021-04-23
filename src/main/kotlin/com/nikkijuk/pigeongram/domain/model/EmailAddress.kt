package com.nikkijuk.pigeongram.domain.model

import java.util.Objects
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * 
 * @param name 
 * @param address 
 */
data class EmailAddress(

    @field:JsonProperty("name", required = true) val name: kotlin.String,

    @field:JsonProperty("address", required = true) val address: kotlin.String
) {

}

