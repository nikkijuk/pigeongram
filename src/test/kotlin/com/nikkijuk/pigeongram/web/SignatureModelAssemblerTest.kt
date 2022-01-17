package com.nikkijuk.pigeongram.web

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.jayway.jsonpath.JsonPath
import com.nikkijuk.pigeongram.web.model.Signature
import org.junit.Assert
import org.junit.jupiter.api.Test
import org.springframework.hateoas.EntityModel

internal class SignatureModelAssemblerTest {

    private val assembler = SignatureModelAssembler ()
    private val signature = Signature (id = 1, mailboxId = "m", name = "n", content = "c")

    private val mapper: ObjectMapper = ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT)

    @Test
    fun `creating is successful`() {

        val model: EntityModel<Signature> = assembler.toModel(signature)
        val json = mapper.writeValueAsString(model)
        //println(json)

        // All hrefs from links with rel=='self' as list
        val self : List<String> = JsonPath.parse(json).read("$.links[?(@.rel=='self')].href")
        //println("self is '$self'")

        Assert.assertEquals("/signatures/1", self.firstOrNull())
    }
}