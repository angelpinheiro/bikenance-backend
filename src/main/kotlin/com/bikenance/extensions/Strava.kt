package com.bikenance.extensions

import com.bikenance.api.strava.configureOAuth
import com.bikenance.api.strava.stravaWebhookRouting
import io.ktor.server.application.*


fun Application.configureStrava() {
    configureOAuth()
    stravaWebhookRouting()
}

