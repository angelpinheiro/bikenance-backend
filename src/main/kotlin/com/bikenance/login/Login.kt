package com.bikenance.login

import com.bikenance.routing.login.login
import com.bikenance.routing.login.loginAuthentication
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureLogin() {

    loginAuthentication()

    routing {
        login()
    }
}