package com.nikkijuk.pigeongram.domain

import com.azure.spring.data.cosmos.core.mapping.Container
import com.azure.spring.data.cosmos.core.mapping.PartitionKey

/**
 * Address is embedded within User and is normal POJO
 *
 * Kotlins data classes didn't work here as Jackson didn't manage to live with immutable stuff
 * Lateinit is used as all attributes are mandatory and thus non nullable
 */
class Address {
    lateinit var street: String
    lateinit var postalcode: String
    lateinit var city: String

    // no-arg constuctor is used by Jackson
    constructor() {}

    constructor(street: String, postalcode: String, city: String) {
        this.street = street
        this.postalcode = postalcode
        this.city = city
    }

    @Override
    override fun toString() = "[Address: $street, $postalcode $city]"

}