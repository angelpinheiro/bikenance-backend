package com.bikenance.modules

import com.bikenance.features.strava.StravaConfig
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.*
import kotlin.time.Duration.Companion.seconds

object ReqParams {
    const val CLIENT_ID = "client_id"
    const val CLIENT_SECRET = "client_secret"
    const val CALLBACK_URL = "callback_url"
    const val VERIFY_TOKEN = "verify_token"
}

const val VERIFY_TOKEN = "BIKENANCE_VERIFY_TOKEN"

val client = HttpClient(CIO) {
    install(Logging)
}

fun Application.stravaWebhookRouting(config: StravaConfig) {

    routing {

        /**
         * Returns current strava API subscriptions
         */
        get("/subscriptions") {
            val response = client.get(config.subscriptionUrl) {
                parameter(ReqParams.CLIENT_ID, config.clientId)
                parameter(ReqParams.CLIENT_SECRET, config.clientSecret)
            }
            val responseText = String(response.readBytes())
            call.respondText(responseText, ContentType.parse("application/json"), HttpStatusCode.OK)
        }

        /**
         * Receive strava activity updates
         */
        post("/webhook") {
            val body = call.receiveText()
            println(body)
            call.respond(HttpStatusCode.OK)
        }

        /**
         * Validate subscription endpoint
         */
        get("/webhook") {
            val parameters = call.request.queryParameters
            val mode = parameters["hub.mode"]
            val challenge = parameters["hub.challenge"]
            val verifyToken = parameters["hub.verify_token"]

            println("Webhook: $mode, $challenge, $verifyToken")

            if ("subscribe" == mode && verifyToken == VERIFY_TOKEN) {
                call.respondText(
                    "{\"hub.challenge\":\"$challenge\"}",
                    ContentType.parse("application/json"),
                    HttpStatusCode.OK
                )
            } else {
                call.respond(HttpStatusCode.Forbidden, "")
            }
        }
    }

    if (config.subscribeOnLaunch) {
        subscribeToStravaWebhooks()
    }
}


fun Application.subscribeToStravaWebhooks() {

    // Flow:
    // 1) check subscriptions (* not implemented)
    // 2) if there are no subscriptions, or if current callback_url is not updated, delete old and create a new one

    val scope = CoroutineScope(Job() + Dispatchers.IO)

    val clientId = environment.config.property("strava_webhooks.client_id").getString()
    val clientSecret = environment.config.property("strava_webhooks.client_secret").getString()
    val subscriptionUrl = environment.config.property("strava_webhooks.strava_subscribe_url").getString()
    val apiUrl = environment.config.property("api.url").getString()

    scope.launch {

        //  wait for app engine to deploy routes (refactor)
        delay(5.seconds)
        val response = client.post(subscriptionUrl) {
            parameter(ReqParams.CLIENT_ID, clientId)
            parameter(ReqParams.CLIENT_SECRET, clientSecret)
            parameter(ReqParams.CALLBACK_URL, "$apiUrl/webhook")
            parameter(ReqParams.VERIFY_TOKEN, VERIFY_TOKEN)
        }

        if (response.status == HttpStatusCode.OK) {
            println("SUCCESS: Webhook subscription created.")
        } else {
            println("WARNING: Could not create webhook subscription.")
        }
    }
}

data class WebhookEvent(
    val aspect_type: String,
    val event_time: Long,
    val object_id: Long,
    val object_type: String,
    val owner_id: Long,
    val subscription_id: Long
)


