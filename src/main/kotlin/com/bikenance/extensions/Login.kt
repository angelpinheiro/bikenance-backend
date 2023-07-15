package com.bikenance.extensions

import com.bikenance.data.network.routing.login.login
import com.bikenance.data.network.routing.login.loginAuthentication
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureLogin() {

    loginAuthentication()

    routing {
        login()
    }
}