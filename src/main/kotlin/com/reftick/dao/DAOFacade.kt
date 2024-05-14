package com.reftick.dao

import com.reftick.models.*

interface DAOFacade {
    suspend fun allUsers(): List<User>
    suspend fun findUsername(username: String): User?
    suspend fun addNewUser(user: User): User?
    suspend fun editUser(username: String, firstName: String, lastName: String, email: String, password: String): Boolean
    suspend fun deleteUser(username: String): Boolean
    suspend fun findEmail(email: String): User?
}