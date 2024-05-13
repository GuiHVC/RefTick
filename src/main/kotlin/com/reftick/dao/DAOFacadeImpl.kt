package com.reftick.dao

import com.reftick.dao.DatabaseSingleton.dbQuery
import com.reftick.models.*
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class DAOFacadeImpl : DAOFacade {
    private fun resultRowToUser(row: ResultRow) = User(
        username = row[Users.username],
        firstName = row[Users.firstName],
        lastName = row[Users.lastName],
        email = row[Users.email],
        password = row[Users.password],
    )
    override suspend fun allUsers(): List<User> = dbQuery {
        Users.selectAll().map(::resultRowToUser)
    }

    override suspend fun user(username: String): User? = dbQuery {
        Users
            .select { Users.username eq username }
            .map(::resultRowToUser)
            .singleOrNull()
    }

    override suspend fun addNewUser(user: User): User? = dbQuery {
        val insertStatement = Users.insert {
            it[Users.username] = user.username
            it[Users.firstName] = user.firstName
            it[Users.lastName] = user.lastName
            it[Users.email] = user.email
            it[Users.password] = user.password
        }
        insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToUser)
    }

    override suspend fun editUser(
        username: String,
        firstName: String,
        lastName: String,
        email: String,
        password: String
    ): Boolean = dbQuery {
        Users.update({ Users.username eq username }) {
            it[Users.firstName] = firstName
            it[Users.lastName] = lastName
            it[Users.email] = email
            it[Users.password] = password
        } > 0
    }

    override suspend fun deleteUser(username: String): Boolean = dbQuery {
        Users.deleteWhere { Users.username eq username } > 0
    }
}

val dao: DAOFacade = DAOFacadeImpl().apply {
    runBlocking {
        if(allUsers().isEmpty()) {
            val guil = User("Guil", "Guilherme", "Cunha", "admin1@usp.br", "senha")
            val adrielias = User("Adrielias", "Adriano", "Andrade", "admin2@usp.br", "senha")
            addNewUser(guil)
            addNewUser(adrielias)
        }
    }
}