package com.nikkijuk.pigeongram.web

import com.nikkijuk.pigeongram.domain.service.SignatureService
import com.nikkijuk.pigeongram.web.model.Signature
import com.nikkijuk.pigeongram.web.model.toDomain
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.hateoas.EntityModel
import org.springframework.http.ResponseEntity

internal class SignatureControllerTest {

    private lateinit var service: SignatureService
    private lateinit var assembler: SignatureModelAssembler

    private lateinit var controller: SignatureController

    @BeforeEach
    fun before() {
        service = mockk()
        assembler = SignatureModelAssembler ()
        controller = SignatureController(service,assembler)
    }

    @Test
    fun `creating is successful`() {

        val signature  = Signature (id = 1, mailboxId = "mid", name = "name", content = "content")

        every { service.createSignature(any()) } returns signature.toDomain()

        val result: ResponseEntity<EntityModel<Signature>> = controller.create(signature)

        Assertions.assertFalse(result.body!!.links.isEmpty)

        verify { service.createSignature(any()) }

    }

}
