package com.bikenance.api

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.mainRoutes() {

    get("/ping") {
        call.respond("pong")
    }

}