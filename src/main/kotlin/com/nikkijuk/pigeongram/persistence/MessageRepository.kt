package com.nikkijuk.pigeongram.persistence

import com.nikkijuk.pigeongram.domain.model.EmailMessage
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface MessageRepository : MongoRepository<EmailMessage, String>