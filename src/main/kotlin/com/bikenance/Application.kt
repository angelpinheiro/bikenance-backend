package com.bikenance

import com.bikenance.extensions.configureFirebase
import com.bikenance.data.network.routing.athleteRoutes
import com.bikenance.data.network.routing.mainRoutes
import com.bikenance.data.network.routing.profileRoutes
import com.bikenance.data.network.routing.userRoutes
import com.bikenance.di.appModule
import com.bikenance.extensions.configureMongoDB
import com.bikenance.extensions.configurePlugins
import com.bikenance.extensions.configureStrava
import com.bikenance.extensions.configureLogin
import io.ktor.server.application.*
import io.ktor.server.netty.*
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

    configureFirebase()
    configureStrava()
}
