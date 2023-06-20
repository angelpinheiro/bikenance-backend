package com.bikenance.push

import com.bikenance.login.config.AppConfig
import com.bikenance.model.User
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import io.ktor.client.*
import java.io.File
import java.io.FileInputStream


class MessageSender(
    private val client: HttpClient,
    private val appConfig: AppConfig,
    private val mapper: ObjectMapper
) {

    fun test() {
        println("Firebase app ${FirebaseApp.getInstance().name}")
    }

    init {
        val resource = File("src/main/resources/" + appConfig.firebase.serviceAccountFile)
        val options = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(FileInputStream(resource)))
            .build()

        FirebaseApp.initializeApp(options)

        println("Initialized firebase app ${FirebaseApp.getInstance().name}")
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
}