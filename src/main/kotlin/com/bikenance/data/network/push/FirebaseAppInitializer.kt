package com.bikenance.data.network.push

import com.bikenance.AppConfig
import com.bikenance.util.bknLogger
import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions

class FirebaseAppInitializer {

    val log = bknLogger("FirebaseAppInitializer")
    fun initializeFirebaseApp(appConfig: AppConfig) {
        val loader = ConfigLoader()
        val stream = loader.getConfigFileAsStream(appConfig.firebase.serviceAccountFile)
        val options = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(stream))
            .build()

        try {

            if (!FirebaseApp.getApps().any {
                    it.name == FirebaseApp.DEFAULT_APP_NAME
                }) {
                FirebaseApp.initializeApp(options)
                log.info("Initialized firebase app ${FirebaseApp.getInstance().name}")
            } else {
                log.info("firebase app already initialized")
            }

        } catch (e: Exception) {
            log.error("Could not init firebase. FCM will not work", e)
        }
    }

}