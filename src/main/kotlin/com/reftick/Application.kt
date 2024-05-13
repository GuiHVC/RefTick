package com.reftick

import com.reftick.dao.DatabaseSingleton
import com.reftick.plugins.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.freemarker.*
import io.ktor.server.netty.*
import freemarker.cache.*
import io.ktor.http.*
import io.ktor.server.plugins.cors.routing.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

    fun Application.module() {
        install(CORS) {
            anyHost()
            allowHeader(HttpHeaders.ContentType)
            allowHeader("X-Requested-With")
            allowHeader(HttpHeaders.Origin)
        }
    DatabaseSingleton.init()
    /*install(FreeMarker) {
        templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
    }*/
    configureSerialization()
    configureRouting()
}
