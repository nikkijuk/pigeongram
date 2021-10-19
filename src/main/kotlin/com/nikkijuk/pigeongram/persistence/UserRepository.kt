package com.nikkijuk.pigeongram.persistence

import com.nikkijuk.pigeongram.domain.model.User
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : MongoRepository<User, String> {

    /**
     * Find with matching first name and return thru iterable
     */
    fun findByFirstName(firstName: String): Iterable<User>

    /**
     * Find with id and last name
     * In original example last name was used as partition key so this query would get value from specific partition using id
     */
    fun findByIdAndLastName(id: String, lastName: String): User

}