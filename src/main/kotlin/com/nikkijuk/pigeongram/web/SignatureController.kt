package com.nikkijuk.pigeongram.web

import com.nikkijuk.pigeongram.domain.service.SignatureService
import com.nikkijuk.pigeongram.web.model.toApi
import com.nikkijuk.pigeongram.web.model.toDomain
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.IanaLinkRelations
import org.springframework.hateoas.server.RepresentationModelAssembler
import org.springframework.hateoas.server.core.DummyInvocationUtils.methodOn
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

import com.nikkijuk.pigeongram.domain.model.Signature as SignatureDomain
import com.nikkijuk.pigeongram.web.model.Signature as SignatureApi

@Component
class SignatureModelAssembler :
    RepresentationModelAssembler<SignatureApi, EntityModel<SignatureApi>> {
    override fun toModel(signature: SignatureApi): EntityModel<SignatureApi> {
        return EntityModel.of(signature,
            linkTo(methodOn(SignatureController::class.java).one(signature.id)).withSelfRel(),
            linkTo(methodOn(SignatureController::class.java).all()).withRel("signatures")
        )
    }
}

/**
 * Handwritten signature controller uses spring hateoas
 *
 * This class is created to see how
 *
 * ./gradlew clean generateOpenApiDocs
 *
 * generates open api definitions to /docs/api.json
 *
 * please see tutorial at: https://spring.io/guides/tutorials/rest/
 */
@RestController
@RequestMapping(value = ["/signatures"], produces = [MediaType.APPLICATION_JSON_VALUE])
class SignatureController (val service: SignatureService, val assembler: SignatureModelAssembler)  {

    @PostMapping("/")
    fun create(@RequestBody newSignature: SignatureApi): ResponseEntity<EntityModel<SignatureApi>> {
        val createdSignature: SignatureDomain = service.createSignature(newSignature.toDomain())
        val entityModel: EntityModel<SignatureApi> = assembler.toModel(createdSignature.toApi())

        return ResponseEntity
            .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()) // 201 returned
            .body<EntityModel<SignatureApi>>(entityModel)
    }

    @PutMapping("/{id}")
    fun replace(
        @RequestBody newSignature: SignatureApi,
        @PathVariable id: Long
    ): ResponseEntity<EntityModel<SignatureApi>> {
        val updatedSignature: SignatureDomain = service.retrieve(id)
            .map { service.updateSignature(it.copy(id = id)) }
            .orElseGet { service.createSignature(newSignature.toDomain().copy(id = id)) }

        val entityModel: EntityModel<SignatureApi> = assembler.toModel(updatedSignature.toApi())

        // 201 returned
        return ResponseEntity
            .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
            .body<EntityModel<SignatureApi>>(entityModel)
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete (@PathVariable id: Long): ResponseEntity<SignatureApi> {
        service.deleteSignature(id)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/{id}")
    fun one(@PathVariable id: Long): EntityModel<SignatureApi> {
        val signature: SignatureDomain = service.retrieveOrFail(id)

        return assembler.toModel(signature.toApi())
    }

    @GetMapping("/")
    fun all(): CollectionModel<EntityModel<SignatureApi>> {
        val signatures: List<EntityModel<SignatureApi>> = service.retrieveAll()
            .map{ assembler.toModel(it.toApi()) }

        return CollectionModel.of(signatures,
            linkTo(methodOn(SignatureController::class.java).all()).withSelfRel()
        )
    }

}