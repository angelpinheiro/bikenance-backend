package com.bikenance

import com.bikenance.database.mongodb.configureMongoDB
import com.bikenance.login.config.loadConfig
import com.bikenance.login.configureLogin
import com.bikenance.modules.appModule
import com.bikenance.modules.configurePlugins
import com.bikenance.push.MessageSender
import com.bikenance.routing.*
import com.bikenance.strava.configureStrava
import io.ktor.server.application.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger


// Start Server engine
fun main(args: Array<String>) = EngineMain.main(args)

/**
 * App main module, referenced in application.conf
 */
@Suppress("unused")
fun Application.module() {

    val messageSender: MessageSender by inject()


    install(Koin) {
        slf4jLogger()
        modules(appModule)
    }

    loadConfig()

    configurePlugins()

    configureLogin()

    configureStrava()

    configureMongoDB()

    // routes

    routing {
        get("/") {
            call.respond("Bikenance Server running!")
        }
        get("/wh") {
            call.respond("Bikenance Server WH!")
        }
    }

    userRoutes()
    athleteRoutes()
    profileRoutes()
    imageRoutes()
    test()

    messageSender.test()


}
