package com.bikenance.extensions

import com.bikenance.data.network.routing.strava.configureOAuth
import com.bikenance.data.network.routing.strava.stravaWebhookRouting
import io.ktor.server.application.*


fun Application.configureStrava() {
    configureOAuth()
    stravaWebhookRouting()
}

