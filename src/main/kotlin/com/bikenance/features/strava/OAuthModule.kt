package com.bikenance.features.strava

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.logging.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


fun Application.configureOAuth(config: StravaConfig) {

    authentication {
        oauth("auth-oauth-strava") {
            urlProvider = { "${config.apiUrl}/callback" }
            providerLookup = {
                OAuthServerSettings.OAuth2ServerSettings(
                    name = "strava",
                    authorizeUrl = "https://www.strava.com/oauth/authorize",
                    accessTokenUrl = "https://www.strava.com/api/v3/oauth/token",
                    requestMethod = HttpMethod.Post,
                    clientId = config.clientId,
                    clientSecret = config.clientSecret,
                    defaultScopes = listOf("read_all,activity:read_all"),
                )
            }
            client = HttpClient(CIO) {
                install(Logging)
            }
        }
    }

    routing {
        get("/callback") {

            call.parameters.forEach { s, strings ->
                println("$s: ${strings.joinToString(",")}")
            }
            val token = call.parameters["code"]
            call.respondRedirect("/?token=$token")
        }

        authenticate("auth-oauth-strava") {
            get("/strava") {
                call.respondRedirect("${config.apiUrl}/callback")
            }
        }
    }
}