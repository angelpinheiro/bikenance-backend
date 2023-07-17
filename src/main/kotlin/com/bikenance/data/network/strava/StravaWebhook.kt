package com.bikenance.data.network.strava

import com.bikenance.AppConfig
import com.bikenance.api.strava.VERIFY_TOKEN
import com.bikenance.data.model.strava.StravaRequestParams
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.logging.*
import kotlinx.coroutines.*
import kotlin.time.Duration.Companion.seconds

class StravaWebhook(val config: AppConfig, val client: HttpClient, private val mapper: ObjectMapper) {

    fun subscribe() {

        if (!config.strava.subscribeOnLaunch) return

        val log = KtorSimpleLogger("Application - StravaWebhooks")

        // Flow:
        // 1) check subscriptions (* not implemented)
        // 2) if there are no subscriptions, or if current callback_url is not updated, delete old and create a new one

        val scope = CoroutineScope(Job() + Dispatchers.IO)

        scope.launch {

            //  wait for app engine to deploy routes (refactor)
            delay(0.seconds)

            log.info("Calling strava subscribeUrl...")

            val existResponse = client.get(config.strava.subscribeUrl) {
                parameter(StravaRequestParams.CLIENT_ID, config.strava.clientId)
                parameter(StravaRequestParams.CLIENT_SECRET, config.strava.clientSecret)
            }

            log.info("Calling strava subscribeUrl -> Status ${existResponse.status}")

            if (existResponse.status == HttpStatusCode.OK) {

                val subsList = mapper.readValue<List<StravaSubscription>>(existResponse.bodyAsText())
                var deleted = false

                subsList.forEach { sub ->
                    if (sub.callbackUrl != "${config.api.url}/api/webhook" || config.strava.forceSubscribe) {
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
                        parameter(StravaRequestParams.CALLBACK_URL, "${config.api.url}/${config.api.rootPath}/webhook")
                        parameter(StravaRequestParams.VERIFY_TOKEN, VERIFY_TOKEN)
                    }

                    when (response.status) {
                        HttpStatusCode.Created -> {
                            log.info("Strava subscription created.")
                        }

                        HttpStatusCode.ServiceUnavailable -> {
                            log.info("Strava service is temporary unavailable.")
                        }

                        else -> {
                            log.error("Could not create Strava webhook subscription. ${response.status}")
                        }
                    }
                }
            } else {
                when (existResponse.status) {

                    HttpStatusCode.ServiceUnavailable -> {
                        log.info("Strava service is temporary unavailable.")
                    }

                    else -> {
                        log.error("Could not check strava subscription endpoint (${existResponse.status})")
                    }
                }
            }
        }
    }

    data class StravaSubscription(
        @JsonProperty("id") var id: Int? = null,
        @JsonProperty("resource_state") var resourceState: Int? = null,
        @JsonProperty("application_id") var applicationId: Int? = null,
        @JsonProperty("callback_url") var callbackUrl: String? = null,
        @JsonProperty("created_at") var createdAt: String? = null,
        @JsonProperty("updated_at") var updatedAt: String? = null

    )

}