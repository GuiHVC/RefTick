package com.reftick.models

import kotlinx.serialization.Serializable

@Serializable
data class User(val username: String, val firstName: String, val lastName: String,
                val email: String, val password: String)

// Mudar isso depois, a lista deverá ser um arquivo
val userBase = mutableListOf<User>()