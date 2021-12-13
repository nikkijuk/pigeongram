package com.nikkijuk.pigeongram.domain.service

import com.nikkijuk.pigeongram.domain.model.Signature
import com.nikkijuk.pigeongram.persistence.SignatureRepository
import org.springframework.stereotype.Service
import java.util.Optional
import javax.transaction.Transactional

internal class SignatureNotFoundException(id: Long) : RuntimeException("Could not find signature $id")

@Service
class SignatureService (
    val signatureRepository: SignatureRepository
)  {

    fun createSignature(signature: Signature): Signature {
        return signatureRepository.save(signature)
    }

    fun createSignature(mailboxId: String, signature: Signature): Signature {
        return createSignature(signature.copy(mailboxId = mailboxId))
    }

    fun updateSignature(signature: Signature): Signature {
        return signatureRepository.save(signature)
    }

    fun updateSignature(mailboxId: String, signaturId: Long, signature: Signature): Signature {
        val signaturInDB: Signature = retrieveSignatureOfMailbox(mailboxId, signaturId)
        return updateSignature(signaturInDB.copy(name = signature.name, content = signature.content))
    }

    @Transactional
    fun deleteSignature(id : Long) {
        signatureRepository.deleteById(id)
    }

    @Transactional
    fun deleteSignature(mailboxId: String, signaturId: Long) {
        retrieveSignatureOfMailbox(mailboxId, signaturId)
        deleteSignature(signaturId)
    }

    fun deleteAllSignaturesOfMailbox(mailboxId: String) {
        signatureRepository.removeAllByMailboxId(mailboxId)
    }

    fun retrieve(id:Long): Optional<Signature> {
        return signatureRepository.findById(id)
    }

    fun retrieveOrFail(id:Long): Signature {
        return signatureRepository.findById(id)
            .orElseThrow { SignatureNotFoundException(id) }
    }

    fun retrieveAll(): List<Signature> {
        return signatureRepository.findAll()
    }

    fun retrieveAllSignaturenForMailbox(mailboxId: String): List<Signature> {
        return signatureRepository.findAllByMailboxId(mailboxId)
    }

    fun retrieveSignatureOfMailbox(mailboxId: String, signatureId: Long): Signature {
        return signatureRepository.findByIdAndMailboxId(signatureId, mailboxId)
    }

}