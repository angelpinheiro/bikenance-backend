package com.bikenance.data.network.push

import com.bikenance.AppConfig
import com.bikenance.data.model.User
import com.bikenance.util.bknLogger
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import io.ktor.client.*


class MessageSender {

    val log = bknLogger("MessageSender")


    fun sendMessage(user: User, data: MessageData) {

        val registrationId = user.firebaseToken

        if (registrationId != null) {

            log.info("Sending message to  $registrationId")
            val m = Message.builder()
                .putData("app_message_type", data.appMessageType.type)
                .putAllData(data.messageParams)
                .setToken(registrationId)
                .build()

            val response: String = FirebaseMessaging.getInstance().send(m)
            log.info("Successfully sent message: $response to $registrationId")
        } else {
            log.error("Could not send message because registrationId is null")
        }

    }


}