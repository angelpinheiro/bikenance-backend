package com.bikenance.strava

import com.bikenance.strava.webhooks.stravaWebhookRouting
import io.ktor.server.application.*


fun Application.configureStrava() {
    configureOAuth()
    stravaWebhookRouting()
}

