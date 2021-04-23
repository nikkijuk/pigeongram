package com.nikkijuk.pigeongram.persistence

import com.azure.spring.data.cosmos.repository.CosmosRepository
import com.nikkijuk.pigeongram.domain.model.EmailMessage
import org.springframework.stereotype.Repository

@Repository
interface MessageRepository : CosmosRepository<EmailMessage, String> {

}