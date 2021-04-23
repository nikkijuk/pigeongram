package com.nikkijuk.pigeongram.persistence

import com.azure.spring.data.cosmos.repository.CosmosRepository
import com.azure.spring.data.cosmos.repository.Query
import com.nikkijuk.pigeongram.domain.model.User
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : CosmosRepository<User, String> {
    /**
     * Find with matching first name and return thru iterable
     */
    fun findByFirstName(firstName: String): Iterable<User>

    /**
     * Find with id and last name
     * In original example last name was used as partition key so this query would get value from specific partition using id
     */
    fun findByIdAndLastName(id: String, lastName: String): User

    /**
     * get with first name and lastname
     */
    @Query(value = "select * from c where c.firstName = @firstName and c.lastName = @lastName")
    fun getUsersByFirstNameAndLastName(@Param("firstName") firstName: String, @Param("lastName") lastName: String): List<User>

    /**
     * From given offset using limit - no idea how order is kept stable here ...
     */
    @Query(value = "select * from c offset @offset limit @limit")
    fun getUsersWithOffsetLimit(@Param("offset") offset: Int, @Param("limit") limit: Int): List<User>

    /**
     * return users where address has given postal code.
     * Note that wildcard "*" can't be used as query would return c times a as result (not quite what we need here)
     */
    @Query(value = "select c.id, c.firstName, c.lastName, c.addresses from c join a in c.addresses where a.postalcode = @plz")
    fun getUsersByPostalcode(@Param("plz") plz: String): List<User>
}