package com.reftick

import com.reftick.plugins.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.testing.*
import kotlin.test.*
import com.reftick.models.*
import io.ktor.client.request.cookie
import io.ktor.server.application.*
import io.ktor.server.sessions.*
import io.ktor.server.testing.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.SchemaUtils
import com.reftick.dao.DatabaseSingleton
import io.ktor.client.*
import io.ktor.client.plugins.cookies.*
import java.net.URLEncoder
import io.ktor.client.engine.cio.*


class ApplicationTest {

    val Guil = User("Guil", "Guilherme", "Cunha", "admin1@usp.br", "senha")
    val Adrielias = User("Adrielias", "Adriano", "Andrade", "admin2@usp.br", "senha")
    val testUser = User("teste", "teste", "teste", "teste@teste.com", "teste")

    @BeforeTest
    fun setupInMemoryTestDB() {
        // Conecta ao banco de dados H2 em memória
        Database.connect("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;", driver = "org.h2.Driver")

        transaction {
            SchemaUtils.create(Images, Users)
        }
    }

    @AfterTest
    fun clearInMemoryTestDB() {
        // Limpa o banco de dados após cada teste
        transaction {
            SchemaUtils.drop(Images, Users)
        }
    }

    @Test
    fun canListAllUsers() = testApplication {
        application {
            configureSerialization()
            configureRouting()
        }

        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val response = client.get("/")
        assertEquals(HttpStatusCode.OK, response.status, "Failed to list all users")

        val results = response.body<Map<String, List<User>>>()

        assertEquals(listOf(Guil, Adrielias), results.get("user"), "Users not found")
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
            configureSerialization()
            configureRouting()

        }

        val cookieStorage = AcceptAllCookiesStorage()
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
            install(HttpCookies){
                storage = cookieStorage
            }
        }

        val response = client.post("/signup") {
            contentType(ContentType.Application.Json)
            setBody(testUser)
        }

        assertEquals(HttpStatusCode.Created, response.status, "User not created successfully")

        val userList = client.get("/")

        assertEquals(HttpStatusCode.OK, userList.status, "Failed to list all users")

        val results = userList.body<Map<String, List<User>>>()

        assertEquals(listOf(testUser), results.get("user"), "User not found")

    }

    @Test
    fun canLogOut() = testApplication {
        application {
            install(Sessions){
                cookie<UserSession>("UserSession", SessionStorageMemory()){
                    cookie.extensions["SameSite"] = "None"
                    cookie.secure = true
                }//,directorySessionStorage(File("build/.sessions")))
            }
            configureSerialization()
            configureRouting()

        }

        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
            install(HttpCookies)
        }

        val signUpResponse = client.post("/signup") {
            contentType(ContentType.Application.Json)
            setBody(testUser)
        }

        assertEquals(HttpStatusCode.Created, signUpResponse.status, "User not created successfully")


    }
    @Test
    fun canLogIn() = testApplication {
        application {
            install(Sessions){
                cookie<UserSession>("UserSession", SessionStorageMemory()){
                    cookie.extensions["SameSite"] = "None"
                    cookie.secure = true
                }//,directorySessionStorage(File("build/.sessions")))
            }
            configureSerialization()
            configureRouting()
        }

        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val signUpResponse = client.post("/signup") {
            contentType(ContentType.Application.Json)
            setBody(testUser)
        }

        assertEquals(HttpStatusCode.Created, signUpResponse.status, "User not created successfully")

        val logInURL = "/login?email=${URLEncoder.encode(testUser.email, "UTF-8")}&password=${URLEncoder.encode(testUser.password, "UTF-8")}"
        val logInResponse = client.get(logInURL)
        assertEquals(HttpStatusCode.OK, logInResponse.status, "Failed to log in")
    }
}
