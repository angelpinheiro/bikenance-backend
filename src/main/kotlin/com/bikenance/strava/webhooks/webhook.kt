package com.bikenance.strava.webhooks

import com.bikenance.AppConfig
import com.bikenance.database.mongodb.DB
import com.bikenance.strava.model.EventData
import com.bikenance.strava.model.StravaRequestParams
import com.bikenance.strava.usecase.ReceiveDataUseCase
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.logging.*
import kotlinx.coroutines.*
import org.koin.ktor.ext.inject
import kotlin.time.Duration.Companion.seconds


const val VERIFY_TOKEN = "BIKENANCE_VERIFY_TOKEN"

val client = HttpClient(CIO) {
//    install(Logging)
}

fun Application.stravaWebhookRouting() {

    val config: AppConfig by inject()
    val db: DB by inject()
    val strava: com.bikenance.strava.api.Strava by inject()
    val receiveDataUseCase: ReceiveDataUseCase by inject()

    routing {

        /**
         * Returns current strava API subscriptions
         */
        get("/subscriptions") {
            val response = client.get(config.strava.subscribeUrl) {
                parameter(StravaRequestParams.CLIENT_ID, config.strava.clientId)
                parameter(StravaRequestParams.CLIENT_SECRET, config.strava.clientSecret)
            }
            val responseText = String(response.readBytes())
            call.respondText(responseText, ContentType.parse("application/json"), HttpStatusCode.OK)
        }

        /**
         * Receive strava activity updates
         */
        post("/webhook") {
            val eventData = call.receive<EventData>()
            // launch a new coroutine for processing event data
            // and return Ok to avoid strava request cancellation
            application.launch {
                receiveDataUseCase.handleEventData(eventData)
            }
            call.respond(HttpStatusCode.OK)
        }

        get("/webhook_check") {
            call.respondText("Webhook available", ContentType.parse("text/plain"), HttpStatusCode.OK)
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

    if (config.strava.subscribeOnLaunch) {
        subscribeToStravaWebhooks(config)
    }
}


fun Application.subscribeToStravaWebhooks(config: AppConfig) {

    val mapper: ObjectMapper by inject()

    // Flow:
    // 1) check subscriptions (* not implemented)
    // 2) if there are no subscriptions, or if current callback_url is not updated, delete old and create a new one

    val scope = CoroutineScope(Job() + Dispatchers.IO)

    scope.launch {

        //  wait for app engine to deploy routes (refactor)
        delay(0.seconds)

        val existResponse = client.get(config.strava.subscribeUrl) {
            parameter(StravaRequestParams.CLIENT_ID, config.strava.clientId)
            parameter(StravaRequestParams.CLIENT_SECRET, config.strava.clientSecret)
        }

        val subsList = mapper.readValue<List<StravaSubscription>>(existResponse.bodyAsText())
        var deleted = false

        subsList.forEach { sub ->
            if (sub.callbackUrl != "${config.api.url}/webhook" || config.strava.forceSubscribe) {
                log.info("Deleting subscription with id ${sub.id}")
                client.delete(config.strava.subscribeUrl + "/" + sub.id) {
                    parameter("id", sub.id)
                    parameter(StravaRequestParams.CLIENT_ID, config.strava.clientId)
                    parameter(StravaRequestParams.CLIENT_SECRET, config.strava.clientSecret)
                }
                deleted = true
            }
        }

        if (subsList.isEmpty() || deleted) {
            log.info("Creating new subscription...")

            val response = client.post(config.strava.subscribeUrl) {
                parameter(StravaRequestParams.CLIENT_ID, config.strava.clientId)
                parameter(StravaRequestParams.CLIENT_SECRET, config.strava.clientSecret)
                parameter(StravaRequestParams.CALLBACK_URL, "${config.api.url}/webhook")
                parameter(StravaRequestParams.VERIFY_TOKEN, VERIFY_TOKEN)
            }

            println(response.bodyAsText())
            if (response.status == HttpStatusCode.Created) {
                log.info("SUCCESS: Webhook subscription created.")
            } else {
                log.error("ERROR: Could not create webhook subscription. ${response.status}")
            }
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

data class StravaSubscription(
    @JsonProperty("id") var id: Int? = null,
    @JsonProperty("resource_state") var resourceState: Int? = null,
    @JsonProperty("application_id") var applicationId: Int? = null,
    @JsonProperty("callback_url") var callbackUrl: String? = null,
    @JsonProperty("created_at") var createdAt: String? = null,
    @JsonProperty("updated_at") var updatedAt: String? = null

)

