package com.bikenance.data.network.push

import com.bikenance.AppConfig
import com.bikenance.util.bknLogger
import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import io.ktor.server.application.*

class FirebaseAppInitializer {

    val log = bknLogger("FirebaseAppInitializer")
    fun initializeFirebaseApp(appConfig: AppConfig) {
        val loader = ConfigLoader()
        val stream = loader.getConfigFileAsStream(appConfig.firebase.serviceAccountFile)
        val options = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(stream))
            .build()

        try {
            FirebaseApp.initializeApp(options)
            log.info("Initialized firebase app ${FirebaseApp.getInstance().name}")
        } catch (e: Exception) {
            log.error("Could not init firebase. FCM will not work", e)
        }
    }

}