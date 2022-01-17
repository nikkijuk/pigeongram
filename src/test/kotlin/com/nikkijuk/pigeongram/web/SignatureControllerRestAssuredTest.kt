package com.nikkijuk.pigeongram.web

import com.nikkijuk.pigeongram.domain.service.SignatureService
import com.nikkijuk.pigeongram.web.model.toDomain
import io.restassured.http.ContentType
import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import io.restassured.module.mockmvc.RestAssuredMockMvc
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.apache.http.HttpStatus
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import com.nikkijuk.pigeongram.domain.model.Signature as SignatureDomain
import com.nikkijuk.pigeongram.web.model.Signature as SignatureApi

//@SpringBootTest
//@AutoConfigureMockMvc
@WebMvcTest(SignatureController::class)
@ContextConfiguration(classes =
    [SignatureTestApplication::class, SignatureModelAssembler::class, SignatureService::class])
@TestInstance(PER_CLASS)
internal class SignatureControllerRestAssuredTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var signaturService: SignatureService

    private val requestPayload = SignatureApi(1, "i", name = "n", content = "c")
    private val mockedServiceResponse: SignatureDomain = requestPayload.toDomain()

    @BeforeEach
    fun setUp() {
        RestAssuredMockMvc.mockMvc(mockMvc)
        //RestAssuredMockMvc.standaloneSetup(mockMvc)
        //RestAssuredMockMvc.webAppContextSetup(??)

    }

    @Test
    fun `Creating a Signature with mocked service SOULD return CREATED`() {
        whenever(signaturService.createSignature(any())).thenReturn(mockedServiceResponse)

        // fails with "connection refused" at post
        val signatureId: String = Given {
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
