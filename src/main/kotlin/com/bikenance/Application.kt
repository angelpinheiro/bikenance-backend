package com.bikenance

import com.bikenance.database.mongodb.configureMongoDB
import com.bikenance.login.configureLogin
import com.bikenance.modules.appModule
import com.bikenance.modules.configurePlugins
import com.bikenance.push.MessageSender
import com.bikenance.push.configureFirebase
import com.bikenance.routing.*
import com.bikenance.strava.configureStrava
import io.ktor.server.application.*
import io.ktor.server.netty.*
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

    install(Koin) {
        slf4jLogger()
        modules(appModule)
    }

    loadConfig()

    configurePlugins()
    configureLogin()
    configureMongoDB()

    mainRoutes()
    userRoutes()
    athleteRoutes()
    profileRoutes()
    imageRoutes()

    configureFirebase()
    configureStrava()
}
