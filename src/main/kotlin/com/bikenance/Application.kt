package com.bikenance

import com.bikenance.api.exposeApi
import com.bikenance.data.database.BikeDao
import com.bikenance.data.database.BikeRideDao
import com.bikenance.data.model.Bike
import com.bikenance.data.model.BikeType
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


    val dao: BikeDao by inject()
    environment.monitor.subscribe(ServerReady) {

        launch {

            val bike = Bike(
                userId = "1234",
                type = BikeType.Unknown,
                name = "My bike"
            )

            dao.create(bike)

            dao.getByUserId("1234").forEach {
                log.info("${it.name}")
            }

        }
    }


}
