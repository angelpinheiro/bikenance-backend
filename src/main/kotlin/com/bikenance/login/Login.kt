package com.bikenance.login

import com.bikenance.login.routing.login
import com.bikenance.login.routing.loginAuthentication
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureLogin() {

    loginAuthentication()

    routing {
        login()
    }
}