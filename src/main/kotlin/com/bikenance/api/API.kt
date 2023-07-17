package com.bikenance.api

import com.bikenance.api.profile.profileBikeRoutes
import com.bikenance.api.profile.profileRoutes
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Application.exposeApi() {

    routing {

        loginRoutes()
        mainRoutes()

        authenticate {
            userRoutes()
            athleteRoutes()
            profileRoutes()
            profileBikeRoutes()
        }
    }
}
