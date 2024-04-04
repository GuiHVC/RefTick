package com.reftick.routes

import com.reftick.models.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.userRouting() {
    // Corrigir tudo depois, a lista será um arquivo, não uma lista (frase estranha btw)
    route("/user") {
        get {
            if(userBase.isNotEmpty()){
                call.respond(userBase)
            }
            else{
                call.respondText("No user found", status = HttpStatusCode.OK)
            }
        }
        get("{username?}") {
            val username = call.parameters["username"] ?: return@get call.respondText(
                "Missing username",
                status = HttpStatusCode.BadRequest
            )
            val user =
                userBase.find { it.username == username } ?: return@get call.respondText(
                    "No user with username: $username",
                    status = HttpStatusCode.NotFound
                )
            call.respond(user)
        }
        post {
            val user = call.receive<User>()
            userBase.add(user)
            call.respondText("User successfully created!", status = HttpStatusCode.Created)
        }
        delete("{username?}") {
            val username = call.parameters["username"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
            if (userBase.removeIf { it.username == username }) {
                call.respondText("User successfully obliterated!", status = HttpStatusCode.Accepted)
            } else {
                call.respondText("User Not Found", status = HttpStatusCode.NotFound)
            }
        }
    }
}
