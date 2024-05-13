package com.reftick.dao

import com.reftick.models.*

interface DAOFacade {
    suspend fun allUsers(): List<User>
    suspend fun user(username: String): User?
    suspend fun addNewUser(username: String, firstName: String, lastName: String, email: String, password: String): User?
    suspend fun editUser(username: String, firstName: String, lastName: String, email: String, password: String): Boolean
    suspend fun deleteUser(username: String): Boolean
}