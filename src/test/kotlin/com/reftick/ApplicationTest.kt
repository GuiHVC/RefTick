package com.reftick

import com.reftick.dao.DatabaseSingleton
import com.reftick.plugins.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.testing.*
import kotlin.test.*
import com.reftick.dao.dao
import com.reftick.models.*
import io.ktor.client.request.cookie
import io.ktor.server.application.*
import io.ktor.server.sessions.*

class ApplicationTest {
    @Test
    fun canListUsers() = testApplication {
        application {
            DatabaseSingleton.init()
            configureSerialization()
            configureRouting()
        }

        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val responseList = client.get("/")
        val resultsList = responseList.body<Map<String, List<User>>>()

        assertEquals(HttpStatusCode.OK, responseList.status, "Bad status code for list of users")

        assertEquals(mapOf("user" to dao.allUsers()), resultsList, "List of users does not match")
    }
    @Test
    fun canSearchUser() = testApplication {
        application {
            DatabaseSingleton.init()
            configureSerialization()
            configureRouting()
        }

        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val response = client.get("/Guil")
        val results = response.body<Map<String, User>>()

        assertEquals(HttpStatusCode.OK, response.status, "Admin Guil not found")

        assertEquals(mapOf("user" to User("Guil", "Guilherme", "Cunha", "admin1@usp.br", "senha")), results)
    }
    @Test
    fun canSignUp() = testApplication {
        application {
            install(Sessions){
                cookie<UserSession>("UserSession", SessionStorageMemory()){
                    cookie.extensions["SameSite"] = "None"
                    cookie.secure = true
                }//,directorySessionStorage(File("build/.sessions")))
            }
            DatabaseSingleton.init()
            configureSerialization()
            configureRouting()
        }

        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val responseSignUp = client.post("/signup") {
            contentType(ContentType.Application.Json)
            setBody(User("test", "test", "test", "test", "test"))
        }

        assertEquals(HttpStatusCode.Created, responseSignUp.status, "User not created successfully")

        val responseSearch = client.get("/test")
        assertEquals(HttpStatusCode.OK, responseSearch.status, "User not found after creation")
        val testUser = User("test", "test", "test", "test", "test")
        assertEquals(mapOf("user" to testUser), responseSearch.body(), "User created does not match user found")

    }
    /*@Test
    fun canLogIn() = testApplication {
        application {
            install(Sessions){
                cookie<UserSession>("UserSession", SessionStorageMemory()){
                    cookie.extensions["SameSite"] = "None"
                    cookie.secure = true
                }//,directorySessionStorage(File("build/.sessions")))
            }
            DatabaseSingleton.init()
            configureSerialization()
            configureRouting()
        }

        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val response = client.get("/login") {
            //contentType(ContentType.Application.Json)
            setBody("email: admin1@usp.br" +
                    "password: senha")
        }
        assertEquals(HttpStatusCode.OK, response.status)
    }*/
    /*@Test
    fun canDeleteUser() = testApplication {
        application {
            install(Sessions){
                cookie<UserSession>("UserSession", SessionStorageMemory()){
                    cookie.extensions["SameSite"] = "None"
                    cookie.secure = true
                }//,directorySessionStorage(File("build/.sessions")))
            }
            DatabaseSingleton.init()
            configureSerialization()
            configureRouting()
        }

        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val responseDelete = client.get("/kill")
        assertEquals(HttpStatusCode.OK, responseDelete.status)

        val responseSearch = client.get("/Guil")
        assertEquals(HttpStatusCode.OK, responseSearch.status)
        assertEquals(mapOf("user" to null), responseSearch.body())
    }*/
}
