package com.bikenance.features.strava

import com.bikenance.features.strava.routing.stravaWebhookRouting
import io.ktor.server.application.*


fun Application.configureStrava() {
    configureOAuth()
    stravaWebhookRouting()
}

