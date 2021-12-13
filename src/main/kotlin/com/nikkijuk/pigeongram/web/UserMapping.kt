package com.nikkijuk.pigeongram.web

import com.nikkijuk.pigeongram.domain.model.Address as AddressDomain
import com.nikkijuk.pigeongram.domain.model.User as UserDomain
import com.nikkijuk.pigeongram.generated.model.Address as AddressApi
import com.nikkijuk.pigeongram.generated.model.User as UserApi

// to api
fun UserDomain.toApi () =
        UserApi (
                id = this.id,
                firstName = this.firstName,
                lastName = this.lastName,
                addresses = this.addresses?.map { it.toApi() } ?: listOf(),
        )

fun AddressDomain.toApi () =
        AddressApi (
                street = this.street,
                postalcode = this.postalcode,
                city = this.city,
        )
// to entity
fun UserApi.toDomain () =
        UserDomain (
                id = this.id,
                firstName = this.firstName,
                lastName = this.lastName,
                // return empty list in case of null
                addresses = this.addresses ?.map { it.toDomain() } ?: listOf(),
        )

fun AddressApi.toDomain () =
        AddressDomain (
                street = this.street,
                postalcode = this.postalcode,
                city = this.city,
        )
