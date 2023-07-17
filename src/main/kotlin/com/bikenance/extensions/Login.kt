package com.bikenance.extensions

import com.bikenance.api.login
import com.bikenance.data.network.loginAuthentication
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureLogin() {

    loginAuthentication()

    routing {
        login()
    }
}