package com.nikkijuk.pigeongram.web

import com.nikkijuk.pigeongram.PigeongramApplicationKt
import com.nikkijuk.pigeongram.domain.model.Signature
import com.nikkijuk.pigeongram.persistence.SignatureRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.IanaLinkRelations
import org.springframework.hateoas.server.RepresentationModelAssembler
import org.springframework.hateoas.server.core.DummyInvocationUtils.methodOn
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController


internal class SignatureNotFoundException(id: Long) : RuntimeException("Could not find signature $id")

@Component
class SignatureModelAssembler :
    RepresentationModelAssembler<Signature, EntityModel<Signature>> {
    override fun toModel(signature: Signature): EntityModel<Signature> {
        return EntityModel.of(signature,
            linkTo(methodOn(SignatureController::class.java).one(signature.id)).withSelfRel(),
            linkTo(methodOn(SignatureController::class.java).all()).withRel("signatures")
        )
    }
}

/**
 * Handwritten signature controller uses spring hateoas
 *
 * please see tutorial at: https://spring.io/guides/tutorials/rest/
 *
 * NOTE: uses entities as dto's --
 * might be that I'll add here separate dto's & mapping
 * to be closer to ideas of hexagonal architecture and to study how to separate concerns cleanly
 */
@RestController
class SignatureController (
    val repository: SignatureRepository,
    val assembler: SignatureModelAssembler
)  {

    private val logger: Logger = LoggerFactory.getLogger(PigeongramApplicationKt::class.java)

    @PostMapping("/signatures")
    fun create(@RequestBody newSignature: Signature): ResponseEntity<EntityModel<Signature>> {
        val entityModel: EntityModel<Signature> =
            assembler.toModel(repository.save(newSignature))

        // 201 returned
        return ResponseEntity
            .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
            .body<EntityModel<Signature>>(entityModel)
    }

    @PutMapping("/signatures/{id}")
    fun replace(
        @RequestBody newSignature: Signature,
        @PathVariable id: Long
    ): ResponseEntity<EntityModel<Signature>> {
        val updatedSignature: Signature = repository.findById(id)
            .map { repository.save(it.copy(id = id)) }
            .orElseGet { repository.save(newSignature.copy(id = id)) }

        val entityModel: EntityModel<Signature> = assembler.toModel(updatedSignature)

        // 201 returned
        return ResponseEntity
            .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
            .body<EntityModel<Signature>>(entityModel)
    }

    @DeleteMapping("/signatures/{id}")
    fun delete (@PathVariable id: Long): ResponseEntity<Signature> {
        repository.deleteById(id)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/signatures/{id}")
    fun one(@PathVariable id: Long): EntityModel<Signature> {
        val signature: Signature = repository.findById(id)
            .orElseThrow { SignatureNotFoundException(id) }

        return assembler.toModel(signature)
    }

    @GetMapping("/signatures")
    fun all(): CollectionModel<EntityModel<Signature>> {
        val signatures: List<EntityModel<Signature>> = repository.findAll()
            .map(assembler::toModel)

        return CollectionModel.of(
            signatures,
            linkTo(methodOn(SignatureController::class.java).all()).withSelfRel()
        )
    }

}
