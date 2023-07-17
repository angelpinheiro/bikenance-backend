package com.bikenance

import com.bikenance.api.api
import com.bikenance.di.appModule
import com.bikenance.extensions.*
import com.bikenance.util.listAllRoutes
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

    api()

    configureFirebase()
    configureStrava()

    listAllRoutes()


}
