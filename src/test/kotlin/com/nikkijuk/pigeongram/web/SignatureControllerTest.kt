package com.nikkijuk.pigeongram.web

import com.nikkijuk.pigeongram.domain.service.SignatureService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.Link
import org.springframework.http.ResponseEntity
import com.nikkijuk.pigeongram.domain.model.Signature as SignatureDomain
import com.nikkijuk.pigeongram.web.model.Signature as SignatureApi

internal class SignatureControllerTest {

    private lateinit var service: SignatureService
    private lateinit var assembler: SignatureModelAssembler

    private lateinit var controller: SignatureController

    @BeforeEach
    fun before() {
        service = mockk()
        assembler = mockk ()
        controller = SignatureController(service,assembler)
    }

    @Test
    fun `creating is successful`() {
        val signatureDomain  = SignatureDomain (
            id = 1,
            mailboxId = "mid",
            name = "name",
            content = "content"
            )

        val signatureApi  = SignatureApi (
            id = 1,
            mailboxId = "mid",
            name = "name",
            content = "content"
        )

        every { assembler.toModel(any()) } returns EntityModel.of(signatureApi).add(Link.of("localhost","self"))

        every { service.createSignature(any()) } returns signatureDomain

        val result: ResponseEntity<EntityModel<SignatureApi>> = controller.create(signatureApi)

        Assertions.assertFalse(result.body!!.links.isEmpty)

        verify { service.createSignature(any()) }

        verify { assembler.toModel(any()) }

    }

}
