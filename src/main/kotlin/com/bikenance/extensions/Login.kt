package com.bikenance.extensions

import com.bikenance.data.network.loginAuthentication
import io.ktor.server.application.*

fun Application.configureLogin() {
    loginAuthentication()
}