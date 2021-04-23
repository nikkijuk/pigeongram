package com.nikkijuk.pigeongram.domain

import com.nikkijuk.pigeongram.domain.model.Address
import com.nikkijuk.pigeongram.domain.model.User


// to api
fun User.toApi () =
        com.nikkijuk.pigeongram.generated.model.User (
                id = this.id,
                firstName = this.firstName,
                lastName = this.lastName,
                addresses = this.addresses?.map { it.toApi() } ?: listOf())

fun Address.toApi () =
                com.nikkijuk.pigeongram.generated.model.Address (
                        street = this.street,
                        postalcode = this.postalcode,
                        city = this.city)
// to entity
fun com.nikkijuk.pigeongram.generated.model.User.toEntity () =
        User (
                id = this.id,
                firstName = this.firstName,
                lastName = this.lastName,
                // return empty list in case of null
                addresses = this.addresses ?.map { it.toEntity() } ?: listOf())

fun com.nikkijuk.pigeongram.generated.model.Address.toEntity () =
        Address (
                street = this.street,
                postalcode = this.postalcode,
                city = this.city)
