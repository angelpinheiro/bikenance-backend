package com.bikenance

import com.bikenance.api.exposeApi
import com.bikenance.data.database.mongodb.DAOS
import com.bikenance.di.appModule
import com.bikenance.di.dataModule
import com.bikenance.di.stravaModule
import com.bikenance.extensions.*
import io.ktor.server.application.*
import io.ktor.server.netty.*
import kotlinx.coroutines.launch
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

    /*val daos: DAOS by inject()

    environment.monitor.subscribe(ServerReady) {
        launch {
            daos.bikeRideDao.all().forEach { ride ->
                val updated = ride.copy(averageSpeed = ride.averageSpeed?.let { (it * 100).toInt() / 100.0 })
                log.debug("Updating ride avg speed from ${ride.averageSpeed} to ${updated.averageSpeed}")
                daos.bikeRideDao.update(ride.oid(), updated)
            }
        }

    }*/


}
