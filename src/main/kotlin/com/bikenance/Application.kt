package com.bikenance

import com.bikenance.features.login.configureLogin
import com.bikenance.features.strava.configureStrava
import com.bikenance.modules.appModule
import com.bikenance.modules.configureServer
import com.bikenance.routing.athleteRoutes
import com.bikenance.routing.userRoutes
import io.ktor.server.application.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger


// Start Server engine
fun main(args: Array<String>) = EngineMain.main(args)

/**
 * App main module, referenced in application.conf
 */
@Suppress("unused")
fun Application.module() {

    install(Koin) {
        slf4jLogger()
        modules(appModule)
    }

    configureServer()
    configureStrava()
    configureLogin()
    userRoutes()
    athleteRoutes()

    routing {
        get("/") {
            call.respond("Bikenance Server running!")
        }
    }


}
