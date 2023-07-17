package com.bikenance

import com.bikenance.api.exposeApi
import com.bikenance.data.database.BikeRideDao
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
import java.time.LocalDateTime


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

//
//    val rideDao: BikeRideDao by inject()
//
//    environment.monitor.subscribe(ServerReady) {
//
//        launch {
//            log.info("Looking for rides in the past 2 months")
//            val rides1 = rideDao.getByBikeIdAfter("64b518a6a4f2433e33fe129e", LocalDateTime.now().minusMonths(2))
//            log.info("Rides found ${rides1.size}")
//            log.info("Looking for rides in the past 5 months")
//            val rides2 = rideDao.getByBikeIdAfter("64b518a6a4f2433e33fe129e", LocalDateTime.now().minusMonths(5))
//            log.info("Rides found ${rides2.size}")
//        }
//    }


}
