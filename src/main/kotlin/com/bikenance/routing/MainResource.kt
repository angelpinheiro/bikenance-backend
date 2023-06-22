package com.bikenance.routing

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.mainRoutes() {
    routing {
        get("/") {
            call.respond("Bikenance Server running!")
        }
    }
}