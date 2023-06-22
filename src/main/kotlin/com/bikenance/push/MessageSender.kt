package com.bikenance.push

import com.bikenance.AppConfig
import com.bikenance.model.User
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import io.ktor.client.*
import io.ktor.util.logging.*
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException


class MessageSender(
    private val client: HttpClient,
    private val appConfig: AppConfig,
    private val mapper: ObjectMapper,
) {

    private val log = KtorSimpleLogger(javaClass.simpleName)

    init {
        val configFile = loadConFirebaseConfigFile(appConfig)
        log.info("Config file loaded from ${configFile.absolutePath}")
        val options = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(FileInputStream(configFile)))
            .build()

        FirebaseApp.initializeApp(options)

        log.info("Initialized firebase app ${FirebaseApp.getInstance().name}")
    }

    fun sendMessage(user: User, data: MessageData) {

        val registrationId = user.firebaseToken

        println("Sending message to  $registrationId")
        val m = Message.builder()
            .putData("app_message_type", data.appMessageType.type)
            .putAllData(data.messageParams)
            .setToken(registrationId)
            .build()

        val response: String = FirebaseMessaging.getInstance().send(m)
        println("Successfully sent message: $response")
    }


    private fun loadConFirebaseConfigFile(appConfig: AppConfig): File {

        try {
            val url = {}.javaClass.classLoader.getResource(appConfig.firebase.serviceAccountFile)
            url?.let {
                return File(it.path)
            }
        } catch (e: Exception) {
            log.info("(1/3) Could not load ${appConfig.firebase.serviceAccountFile} from resources")
        }

        try {
            val configFile = File("src/main/resources/" + appConfig.firebase.serviceAccountFile)
            if (configFile.exists() && configFile.isFile)
                return configFile
        } catch (e: Exception) {
            log.info("(1/3) Could not load ${"src/main/resources/" + appConfig.firebase.serviceAccountFile}")
        }

        try {
            val configFile = File(appConfig.firebase.serviceAccountFile)
            if (configFile.exists() && configFile.isFile) {
                return configFile
            }

        } catch (e: Exception) {
            log.info("(3/3) Could not load ${appConfig.firebase.serviceAccountFile}")
        }

        throw FileNotFoundException("File not found: ${appConfig.firebase.serviceAccountFile}")
    }
}