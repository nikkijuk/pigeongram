package com.nikkijuk.pigeongram.web

import com.nikkijuk.pigeongram.domain.service.SignatureService
import com.nikkijuk.pigeongram.web.model.toDomain
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import com.nikkijuk.pigeongram.domain.model.Signature as SignatureDomain
import com.nikkijuk.pigeongram.web.model.Signature as SignatureApi

@WebMvcTest(SignatureController::class)
@ContextConfiguration(classes = [SignatureTestApplication::class, SignatureModelAssembler::class, SignatureService::class])
@TestInstance(PER_CLASS)
internal class SignatureControllerIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var signaturService: SignatureService

    private val requestPayload: SignatureApi = SignatureApi(1, "i", name = "n", content = "c")
    private val mockedServiceResponse: SignatureDomain = requestPayload.toDomain()

    @BeforeAll
    fun `Create test data`() {
    }

    @Test
    fun `Creating a Signature with mocked service SOULD return OK`() {
        whenever(signaturService.createSignature(any())).thenReturn(mockedServiceResponse)

        mockMvc.post("/signatures/") {
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
            content = Json.encodeToString(requestPayload)
        }.andDo { print() }
        .andExpect { status { isCreated() } }

    }

}
