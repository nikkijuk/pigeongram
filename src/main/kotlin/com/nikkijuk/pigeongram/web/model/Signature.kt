package com.nikkijuk.pigeongram.web.model

import com.nikkijuk.pigeongram.domain.model.Signature as SignatureDomain

data class Signature (val id: Long, val mailboxId: String, val name: String, var content: String)

fun SignatureDomain.toApi () = Signature(
    id = this.id, mailboxId = this.mailboxId, name = this.name, content = this.content)

fun Signature.toDomain () = SignatureDomain(
    id = this.id, mailboxId = this.mailboxId, name = this.name, content = this.content)
