package com.nikkijuk.pigeongram.web

import com.nikkijuk.pigeongram.SignatureTestApplication
import com.nikkijuk.pigeongram.domain.service.SignatureService
import com.nikkijuk.pigeongram.web.model.toDomain
import io.restassured.http.ContentType
import io.restassured.module.mockmvc.RestAssuredMockMvc
import io.restassured.module.mockmvc.kotlin.extensions.Extract
import io.restassured.module.mockmvc.kotlin.extensions.Given
import io.restassured.module.mockmvc.kotlin.extensions.Then
import io.restassured.module.mockmvc.kotlin.extensions.When
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.apache.http.HttpStatus
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import com.nikkijuk.pigeongram.domain.model.Signature as SignatureDomain
import com.nikkijuk.pigeongram.web.model.Signature as SignatureApi

@WebMvcTest(SignatureController::class)
@ContextConfiguration(classes =
    [SignatureTestApplication::class, SignatureModelAssembler::class, SignatureService::class])
internal class SignatureControllerMockMvcRestAssuredTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var signaturService: SignatureService

    private val requestPayload = SignatureApi(1, "i", name = "n", content = "c")
    private val mockedServiceResponse: SignatureDomain = requestPayload.toDomain()

    @BeforeEach
    fun setUp() {
        RestAssuredMockMvc.mockMvc(mockMvc)
    }

    @Test
    fun `Creating a Signature with mocked service SOULD return CREATED`() {
        whenever(signaturService.createSignature(any())).thenReturn(mockedServiceResponse)

        // Note: Kotlin extensions are really chick! But they might use wrong class
        // if you have wrong dependency in gradle -- hard to notice while learning ..
        //
        // looked for hours --> fails with "connection refused" at post
        //
        // https://stackoverflow.com/questions/23782160/restassuredmockmvc-connection-to-http-localhost8080-refused/25241722#25241722
        // You are statically importing the wrong methods.
        // You're importing given from com.jayway.restassured.RestAssured (io.restassured.RestAssured if using version 3.x)
        // but you should statically import the methods from com.jayway.restassured.module.mockmvc.RestAssuredMockMvc
        // (io.restassured.module.mockmvc.RestAssuredMockMvc in version 3.x).

        // this is code without kotlin extensions
        /*
        val signatureId: Int =
        given ()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .body(Json.encodeToString(requestPayload))
        .`when`()
            .post("/signatures/")
        .then()
            .statusCode(HttpStatus.SC_CREATED)
        .extract()
            .path("id")
         */

        // really nice
        val signatureId: Int = Given {
            contentType(ContentType.JSON)
            accept(ContentType.JSON)
            body(Json.encodeToString(requestPayload))
        } When {
            post("/signatures/")
        } Then {
            statusCode(HttpStatus.SC_CREATED)
        } Extract {
            path("id")
        }

        Assertions.assertNotNull(signatureId)
    }
}
