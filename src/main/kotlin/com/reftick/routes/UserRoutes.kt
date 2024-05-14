package com.reftick.routes

import com.reftick.models.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import com.reftick.dao.*
import io.ktor.server.freemarker.*
import io.ktor.server.sessions.*
import io.ktor.server.util.*
import java.net.URLDecoder

fun Route.userRouting() {
    // Corrigir tudo depois, a lista será um arquivo, não uma lista (frase estranha btw)
    route("/") {
        get {
            /*if(userBase.isNotEmpty()){
                call.respond(userBase)
            }
            else{
                call.respondText("No user found", status = HttpStatusCode.OK)
            }*/
            //call.respond(FreeMarkerContent("index.ftl", mapOf("users" to dao.allUsers())))
            call.respond(mapOf("user" to dao.allUsers()))
        }
        get("{username}") {
            /*val username = call.parameters["username"] ?: return@get call.respondText(
                "Missing username",
                status = HttpStatusCode.BadRequest
            )
            val user =
                userBase.find { it.username == username } ?: return@get call.respondText(
                    "No user with username: $username",
                    status = HttpStatusCode.NotFound
                )
            call.respond(user)*/
            val username = call.parameters.getOrFail("username")
            //call.respond(FreeMarkerContent("show.ftl", mapOf("user" to dao.user(username))))
            call.respond(mapOf("user" to dao.findUsername(username)))
        }
        get("{username}/edit") {
            val username = call.parameters.getOrFail("username")
            //call.respond(FreeMarkerContent("edit.ftl", mapOf("user" to dao.user(username))))
            call.respond(mapOf("user" to dao.findUsername(username)))
        }
        /*post {
            val user = call.receive<User>()
            // userBase.add(user)
            dao.addNewUser(user)
            call.respondText("User successfully created!", status = HttpStatusCode.Created)
            /*val formParameters = call.receiveParameters()
            val username = formParameters.getOrFail("username")
            val firstName = formParameters.getOrFail("firstName")
            val lastName = formParameters.getOrFail("lastName")
            val email = formParameters.getOrFail("email")
            val password = formParameters.getOrFail("password")
            val user = dao.addNewUser(username, firstName, lastName, email, password)2
            call.respondRedirect("/users/${user?.username}")*/
        } */
        /*delete {
            val session = call.sessions.get<UserSession>()
            if (session == null) {
                call.respondText("Not logged in", status = HttpStatusCode.BadRequest)
                return@delete
            }
            dao.deleteUser(session.id)
            call.sessions.clear<UserSession>()
            call.respondText("User deleted", status = HttpStatusCode.OK)

            /* if (userBase.removeIf { it.username == username }) {
                call.respondText("User successfully obliterated!", status = HttpStatusCode.Accepted)
            } else {
                call.respondText("User Not Found", status = HttpStatusCode.NotFound)
            } */

        }*/
        /*post("{username}") {
            val username = call.parameters.getOrFail("username")
            val formParameters = call.receiveParameters()
            when (formParameters.getOrFail("_action")) {
                "update" -> {
                    val firstName = formParameters.getOrFail("firstName")
                    val lastName = formParameters.getOrFail("lastName")
                    val email = formParameters.getOrFail("email")
                    val password = formParameters.getOrFail("password")
                    dao.editUser(username, firstName, lastName, email, password)
                    call.respondRedirect("/users/$username")
                }
                "delete" -> {
                    dao.deleteUser(username)
                    call.respondRedirect("/users")
                }
            }
        } */
    }
    route("/signup") {
        post {
            val user = call.receive<User>()
            if (dao.findUsername(user.username) != null) {
                call.respondText("User already exists", status = HttpStatusCode.Conflict)
                return@post
            }
            else if (dao.findEmail(user.email) != null) {
                call.respondText("Email already exists", status = HttpStatusCode.Conflict)
                return@post
            }
            else {
                dao.addNewUser(user)
                call.respondText("User successfully created!", status = HttpStatusCode.Created)
            }
        }
    }
    route("/login") {
        get {
            val email = URLDecoder.decode(call.request.queryParameters["email"], "UTF-8") ?: return@get call.respond(
                HttpStatusCode.BadRequest
            )
            val password = URLDecoder.decode(call.request.queryParameters["password"], "UTF-8") ?: return@get call.respond(
                HttpStatusCode.BadRequest
            )
            val user = dao.findEmail(email)
            if (user != null) {
                if(user.password == password){
                    call.sessions.set(UserSession(id = user.username))
                    call.respondText("User logged in", status = HttpStatusCode.OK)
                }
                else{
                    call.respondText("Incorrect password", status = HttpStatusCode.Unauthorized)
                }
            } else {
                call.respondText("User not found", status = HttpStatusCode.Unauthorized)
            }
        }
    }
    route("/logout") {
        get {
            call.sessions.clear<UserSession>()
            call.respondText("User logged out", status = HttpStatusCode.OK)
        }
    }
    route("/kill") {
        get {
            val session = call.sessions.get<UserSession>()
            if (session == null) {
                call.respondText("Not logged in", status = HttpStatusCode.BadRequest)
                return@get
            }
            dao.deleteUser(session.id)
            call.sessions.clear<UserSession>()
            call.respondText("User deleted", status = HttpStatusCode.OK)
        }
    }
}
