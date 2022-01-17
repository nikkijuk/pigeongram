package com.nikkijuk.pigeongram.web

import com.nikkijuk.pigeongram.PigeongramApplicationKt
import io.restassured.http.ContentType
import io.restassured.module.mockmvc.kotlin.extensions.Extract
import io.restassured.module.mockmvc.kotlin.extensions.Given
import io.restassured.module.mockmvc.kotlin.extensions.Then
import io.restassured.module.mockmvc.kotlin.extensions.When
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.apache.http.HttpStatus
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import com.nikkijuk.pigeongram.web.model.Signature as SignatureApi

// OK: this test fails because spring context building is not configured in right way
// this is ok -- I might want to invest more at some point, but now I don't really need heavy
// black box tests

@SpringBootTest (classes = [PigeongramApplicationKt::class])
@AutoConfigureMockMvc
internal class SignatureControllerSpringBootTestRestAssuredTest {

    @LocalServerPort
    private var port : Int = 0

    private val requestPayload = SignatureApi(1, "i", name = "n", content = "c")

    @Test
    fun `Creating a Signature with mocked service SOULD return CREATED`() {

        val signatureId: Int = Given {
            contentType(ContentType.JSON)
            accept(ContentType.JSON)
            body(Json.encodeToString(requestPayload))
        } When {
            post("http://localhost:$port/signatures/")
        } Then {
            statusCode(HttpStatus.SC_CREATED)
        } Extract {
            path("id")
        }

        Assertions.assertNotNull(signatureId)
    }
}
