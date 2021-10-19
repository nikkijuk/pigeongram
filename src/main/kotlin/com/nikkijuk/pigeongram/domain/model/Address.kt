package com.nikkijuk.pigeongram.domain.model

/**
 * Address is embedded within User and is normal POJO
 */
data class Address(
    val street: String,
    val postalcode: String,
    val city: String,
)