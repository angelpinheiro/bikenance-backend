package com.bikenance.api.strava

import com.bikenance.AppConfig
import com.bikenance.data.model.strava.EventData
import com.bikenance.data.model.strava.StravaRequestParams
import com.bikenance.usecase.strava.ReceiveDataUseCase
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.launch
import org.koin.ktor.ext.inject


const val VERIFY_TOKEN = "BIKENANCE_VERIFY_TOKEN"


fun Route.stravaWebhookRoutes() {

    val config: AppConfig by inject()
    val receiveDataUseCase: ReceiveDataUseCase by inject()
    val client: HttpClient by inject()

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




