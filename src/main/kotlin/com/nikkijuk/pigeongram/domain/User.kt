package com.nikkijuk.pigeongram.domain

import com.azure.spring.data.cosmos.core.mapping.Container

/**
 * User is root entity which is to be saved within cosmos db
 *
 * Kotlins data classes didn't work here as Jackson didn't manage to live with immutable stuff
 * Lateinit is used as all attributes are mandatory and thus non nullable
 *
 * @Container annotation defines name of bucket where User objects are to be saved
 * @Container is only needed on root object
 */
@Container(containerName = "myContainer", ru = "400")
class User {
    lateinit var id: String
    lateinit var firstName: String
    lateinit var lastName: String
    lateinit var addresses: List<Address> // nested structure

    // no-arg constuctor is used by Jackson
    constructor() {}

    constructor(id: String, firstName: String, lastName: String, addresses: List<Address>) {
        this.id = id
        this.firstName = firstName
        this.lastName = lastName
        this.addresses = addresses
    }

    @Override
    override fun toString() = "[User: $firstName $lastName, $id @ $addresses]"

}