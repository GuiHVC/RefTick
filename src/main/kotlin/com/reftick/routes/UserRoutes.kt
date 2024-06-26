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
            call.respond(mapOf("user" to dao.allUsers()))
        }
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
                call.sessions.set(UserSession(id = user.username))
                call.respondText("User successfully created!", status = HttpStatusCode.Created)
            }
        }
    }
    route("/login") {
        get {
            if (call.sessions.get<UserSession>() != null) {
                call.respondText("Already logged in", status = HttpStatusCode.BadRequest)
                return@get
            }
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
    route("/upload"){
        post {
            val session = call.sessions.get<UserSession>()
            if (session == null) {
                call.respondText("Not logged in", status = HttpStatusCode.BadRequest)
                return@post
            }
            val receivedImage = call.receive<Image>()
            if (receivedImage.url.length > 1000) {
                call.respondText("Url too big", status = HttpStatusCode.BadRequest)
                return@post
            }
            if (!receivedImage.url.contains(".jpg") && !receivedImage.url.contains(".jpeg") && !receivedImage.url.contains(".png")) {
                call.respondText("Cannot recognize (please try [.jpg], [.jpeg] and [.png] urls", status = HttpStatusCode.BadRequest)
                return@post
            }
            val image = Image(id = 0, url = receivedImage.url, tag = receivedImage.tag, uploader = session.id, author = receivedImage.author)
            img.addImage(image)
            call.respondText("Image uploaded", status = HttpStatusCode.Created)
        }
    }
    route("/challenge"){
        get {
            val tagList = URLDecoder.decode(call.request.queryParameters["tag"], "UTF-8") ?: return@get call.respond(
                HttpStatusCode.BadRequest
            )
            val quantity = URLDecoder.decode(call.request.queryParameters["quantity"], "UTF-8") ?: return@get call.respond(
                HttpStatusCode.BadRequest
            )
            val images = mutableListOf<Image>()
            for(tag in tagList){
                images.addAll(img.imagesByTag(tag.toString()))
            }
            if(images.size >= quantity.toInt()){
                val chosenImages = images.shuffled().take(quantity.toInt())
                call.respond(mapOf("image" to chosenImages))
            }
            else{
                call.respondText("Not enough images", status = HttpStatusCode.NotFound)
            }
        }
    }
    route("/myimages"){
        get {
            val session = call.sessions.get<UserSession>()
            if (session == null) {
                call.respondText("Not logged in", status = HttpStatusCode.BadRequest)
                return@get
            }
            val images = img.imageByUploader(session.id)
            call.respond(mapOf("image" to images))
        }
    }
    route("/deleteimage"){
        get {
            val session = call.sessions.get<UserSession>()
            if (session == null) {
                call.respondText("Not logged in", status = HttpStatusCode.BadRequest)
                return@get
            }
            val id = URLDecoder.decode(call.request.queryParameters["id"], "UTF-8") ?: return@get call.respond(
                HttpStatusCode.BadRequest
            )
            if(img.removeImage(id.toInt())){
                call.respondText("Image deleted", status = HttpStatusCode.OK)
            }
            else{
                call.respondText("Image not found", status = HttpStatusCode.NotFound)
            }
        }
    }
    route("/checkLogin"){
        get {
            val session = call.sessions.get<UserSession>()
            if (session == null) {
                call.respondText("Not logged in", status = HttpStatusCode.BadRequest)
            }
            else{
                call.respondText(session.id, status = HttpStatusCode.OK)
            }
        }
    }
}
