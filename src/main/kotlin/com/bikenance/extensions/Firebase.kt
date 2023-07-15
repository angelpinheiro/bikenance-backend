package com.bikenance.extensions

import com.bikenance.AppConfig
import com.bikenance.data.network.push.ConfigLoader
import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import io.ktor.server.application.*
import org.koin.ktor.ext.inject


fun Application.configureFirebase() {

    val appConfig: AppConfig by inject()

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