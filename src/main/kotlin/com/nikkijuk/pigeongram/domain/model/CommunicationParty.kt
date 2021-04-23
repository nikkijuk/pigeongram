package com.nikkijuk.pigeongram.domain.model

import java.util.Objects
import com.fasterxml.jackson.annotation.JsonProperty
import com.nikkijuk.pigeongram.generated.model.EmailAddress

/**
 * 
 * @param emailAddress 
 */
data class CommunicationParty(

    @field:JsonProperty("emailAddress", required = true) val emailAddress: EmailAddress
) {

}

