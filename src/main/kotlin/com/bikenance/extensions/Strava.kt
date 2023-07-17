package com.bikenance.extensions

import com.bikenance.api.strava.stravaLoginRoutes
import com.bikenance.api.strava.stravaWebhookRoutes
import com.bikenance.data.network.strava.StravaWebhook
import com.bikenance.data.network.strava.stravaAuthentication
import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject


fun Application.configureStrava() {

    val stravaWebhook: StravaWebhook by inject()

    stravaAuthentication()

    routing {
        stravaWebhookRoutes()
        stravaLoginRoutes()
    }

    // wait for server ready and subscribe to strava updates
    environment.monitor.subscribe(ServerReady) {
        stravaWebhook.subscribe()
    }
}

