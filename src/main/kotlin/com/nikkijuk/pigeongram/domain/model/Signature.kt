package com.nikkijuk.pigeongram.domain.model

import org.springframework.data.mongodb.core.mapping.Document

@Document
data class Signature (
    val id: Long,
    val mailboxId: String,
    val name: String,
    var content: String
)