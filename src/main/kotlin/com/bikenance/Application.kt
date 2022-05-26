package com.bikenance

import com.bikenance.database.DatabaseFactory
import com.bikenance.database.UserDao
import com.bikenance.features.login.configureLogin
import com.bikenance.features.strava.configureStrava
import com.bikenance.modules.*
import com.bikenance.routing.userRoutes
import io.ktor.server.application.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

// Start Server engine
fun main(args: Array<String>) = EngineMain.main(args)

/**
 * App main module, referenced in application.conf
 */
@Suppress("unused")
fun Application.module() {

    DatabaseFactory.init()

    configureServer()
    configureStrava()
    configureLogin()

    userRoutes()
}
