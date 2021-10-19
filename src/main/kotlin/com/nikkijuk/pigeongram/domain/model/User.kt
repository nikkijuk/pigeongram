package com.nikkijuk.pigeongram.domain.model

import org.springframework.data.mongodb.core.mapping.Document

/**
 * User is root entity which is to be saved within cosmos db
 *
 * @Document annotation is only needed on root object
 */
@Document
data class User(
    val id: String,
    val firstName: String,
    val lastName: String,
    val addresses: List<Address>? = null,
)