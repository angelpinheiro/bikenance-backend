package com.bikenance.features.login

import com.bikenance.features.login.config.JwtConfig
import com.bikenance.features.login.routing.loginAuthentication
import com.bikenance.features.login.routing.login
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureLogin() {

    loginAuthentication()

    routing {
        login()
    }
}