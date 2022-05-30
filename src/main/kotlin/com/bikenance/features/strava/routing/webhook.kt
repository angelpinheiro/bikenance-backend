package com.bikenance.features.strava.routing

import com.bikenance.database.mongodb.DB
import com.bikenance.features.strava.StravaConfig
import com.bikenance.features.strava.api.Strava
import com.bikenance.features.strava.usecase.EventData
import com.bikenance.features.strava.usecase.ReceiveDataUseCase
import com.bikenance.repository.UserRepository
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
import kotlinx.coroutines.*
import org.koin.ktor.ext.inject
import kotlin.time.Duration.Companion.seconds

object ReqParams {
    const val CLIENT_ID = "client_id"
    const val CLIENT_SECRET = "client_secret"
    const val CALLBACK_URL = "callback_url"
    const val VERIFY_TOKEN = "verify_token"
}

const val VERIFY_TOKEN = "BIKENANCE_VERIFY_TOKEN"

val client = HttpClient(CIO) {
//    install(Logging)
}

fun Application.stravaWebhookRouting(config: StravaConfig) {

    val db: DB by inject()
    val userRepository: UserRepository by inject()
    val strava: Strava by inject()

    val receiveDataUseCase = ReceiveDataUseCase(userRepository)

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
            val eventData = call.receive<EventData>()
            // launch a new coroutine for processing event data
            // and return Ok to avoid strava request cancellation
            application.launch {
                receiveDataUseCase.handleEventData(db, strava, eventData)
            }
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

    val mapper: ObjectMapper by inject()

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

        val existResponse = client.get(subscriptionUrl) {
            parameter(ReqParams.CLIENT_ID, clientId)
            parameter(ReqParams.CLIENT_SECRET, clientSecret)
        }

        val subsList = mapper.readValue<List<StravaSubscription>>(existResponse.bodyAsText())
        var deleted = false

        subsList.forEach { sub ->
            if (sub.callbackUrl != "$apiUrl/webhook") {
                println("Deleting subscription with id ${sub.id}")
                client.delete(subscriptionUrl + "/" + sub.id) {
                    parameter("id", sub.id)
                    parameter(ReqParams.CLIENT_ID, clientId)
                    parameter(ReqParams.CLIENT_SECRET, clientSecret)
                }
                deleted = true
            }
        }

        if (deleted) {
            println("Creating new subscription...")

            val response = client.post(subscriptionUrl) {
                parameter(ReqParams.CLIENT_ID, clientId)
                parameter(ReqParams.CLIENT_SECRET, clientSecret)
                parameter(ReqParams.CALLBACK_URL, "$apiUrl/webhook")
                parameter(ReqParams.VERIFY_TOKEN, VERIFY_TOKEN)
            }

            if (response.status == HttpStatusCode.Created) {
                println("SUCCESS: Webhook subscription created.")
            } else {
                println("WARNING: Could not create webhook subscription. ${response.status}")
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

