package com.bikenance.features.strava

import com.bikenance.features.strava.routing.stravaWebhookRouting
import io.ktor.server.application.*


data class StravaConfig(
    val apiUrl: String,
    val clientId: String,
    val clientSecret: String,
    val subscriptionUrl: String,
    val subscribeOnLaunch: Boolean = false
)

fun Application.configureStrava() {

    val config = StravaConfig(
        environment.config.property("api.url").getString(),
        environment.config.property("strava_webhooks.client_id").getString(),
        environment.config.property("strava_webhooks.client_secret").getString(),
        environment.config.property("strava_webhooks.strava_subscribe_url").getString()
    )

    configureOAuth(config)
    stravaWebhookRouting(config)
}

