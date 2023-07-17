package com.bikenance.extensions

import com.bikenance.AppConfig
import com.bikenance.data.network.push.FirebaseAppInitializer
import io.ktor.server.application.*
import org.koin.ktor.ext.inject


fun Application.configureFirebase() {
    val appConfig: AppConfig by inject()
    FirebaseAppInitializer().initializeFirebaseApp(appConfig)
}