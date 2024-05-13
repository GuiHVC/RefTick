package com.reftick.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*

@Serializable
data class User(val username: String, val firstName: String, val lastName: String,
                val email: String, val password: String)

// Mudar isso depois, a lista deverá ser um arquivo
// val userBase = mutableListOf<User>()

object Users : Table() {
    val username = varchar("username", 255)
    val firstName = varchar("firstName", 255)
    val lastName = varchar("lastName", 255)
    val email = varchar("email", 255)
    val password = varchar("password", 255)


    override val primaryKey = PrimaryKey(username)
}
