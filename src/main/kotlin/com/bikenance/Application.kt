package com.bikenance

import com.bikenance.api.exposeApi
import com.bikenance.di.appModule
import com.bikenance.di.dataModule
import com.bikenance.di.stravaModule
import com.bikenance.extensions.*
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

    // Setup dependency injection
    install(Koin) {
        slf4jLogger()
        modules(appModule, dataModule, stravaModule)
    }

    // Load app config
    loadConfig()

    // Configure app modules
    configurePlugins()
    configureLogin()
    configureMongoDB()
    configureFirebase()
    configureStrava()

    // Deploy api
    exposeApi()
}
