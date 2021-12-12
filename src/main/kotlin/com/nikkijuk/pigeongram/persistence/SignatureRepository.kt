package com.nikkijuk.pigeongram.persistence

import com.nikkijuk.pigeongram.domain.model.Signature
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface SignatureRepository : MongoRepository<Signature, Long>