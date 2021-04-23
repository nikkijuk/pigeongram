package com.nikkijuk.pigeongram.domain


// to api
fun com.nikkijuk.pigeongram.domain.User.toApi () =
        com.nikkijuk.pigeongram.generated.model.User (
                id = this.id,
                firstName = this.firstName,
                lastName = this.lastName,
                addresses = this.addresses?.map { it.toApi() } ?: listOf())

fun com.nikkijuk.pigeongram.domain.Address.toApi () =
                com.nikkijuk.pigeongram.generated.model.Address (
                        street = this.street,
                        postalcode = this.postalcode,
                        city = this.city)
// to entity
fun com.nikkijuk.pigeongram.generated.model.User.toEntity () =
        com.nikkijuk.pigeongram.domain.User (
                id = this.id,
                firstName = this.firstName,
                lastName = this.lastName,
                // return empty list in case of null
                addresses = this.addresses ?.map { it.toEntity() } ?: listOf())

fun com.nikkijuk.pigeongram.generated.model.Address.toEntity () =
        com.nikkijuk.pigeongram.domain.Address (
                street = this.street,
                postalcode = this.postalcode,
                city = this.city)
