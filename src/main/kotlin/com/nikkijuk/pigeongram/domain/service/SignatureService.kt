package com.nikkijuk.pigeongram.domain.service

import com.nikkijuk.pigeongram.domain.model.Signature
import com.nikkijuk.pigeongram.persistence.SignatureRepository
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class SignatureService (
    val signatureRepository: SignatureRepository
)  {

    fun createSignature(mailboxId: String, signature: Signature): Signature {
        return signatureRepository.save(signature.copy(mailboxId = mailboxId))
    }

    fun updateSignature(mailboxId: String, signaturId: Long, signature: Signature): Signature {
        val signaturInDB: Signature = retrieveSignatureOfMailbox(mailboxId, signaturId)
        return signatureRepository.save(signaturInDB.copy(name = signature.name, content = signature.content))
    }

    @Transactional
    fun deleteSignature(mailboxId: String, signaturId: Long) {
        retrieveSignatureOfMailbox(mailboxId, signaturId)
        signatureRepository.deleteById(signaturId)
    }

    fun deleteAllSignaturesOfMailbox(mailboxId: String) {
        signatureRepository.removeAllByMailboxId(mailboxId)
    }

    fun retrieveAllSignaturenForMailbox(mailboxId: String): List<Signature> {
        return signatureRepository.findAllByMailboxId(mailboxId)
    }

    fun retrieveSignatureOfMailbox(mailboxId: String, signatureId: Long): Signature {
        return signatureRepository.findByIdAndMailboxId(signatureId, mailboxId)
    }

}