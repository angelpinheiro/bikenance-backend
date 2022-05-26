package com.bikenance.features.login

import com.bikenance.features.login.routing.loginAuthentication
import com.bikenance.modules.login
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureLogin() {

    val config = JwtConfig()

    loginAuthentication(config)

    routing {
        login(config)
    }
}