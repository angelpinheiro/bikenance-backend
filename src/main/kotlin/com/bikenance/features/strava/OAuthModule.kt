package com.bikenance.features.strava

import com.bikenance.model.UserUpdate
import com.bikenance.repository.UserRepository
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.logging.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject


fun Application.configureOAuth(config: StravaConfig) {

    val clientId = environment.config.property("strava_webhooks.client_id").getString()
    val clientSecret = environment.config.property("strava_webhooks.client_secret").getString()
    val subscriptionUrl = environment.config.property("strava_webhooks.strava_subscribe_url").getString()
    val apiUrl = environment.config.property("api.url").getString()


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
                    defaultScopes = listOf("read_all,activity:read_all,profile:read_all"),
                )
            }
            client = HttpClient(CIO) {
                install(Logging)
            }
        }
    }


    routing {

        /**
         * Route for testing purposes. Authorizes a user using the strava OAuth API,
         * and stores the token in the user with id '1'
         */

        val userRepository: UserRepository by inject()

        authenticate("auth-oauth-strava") {
            get("/strava") {
                call.respondRedirect("${config.apiUrl}/callback")
            }

            get("/callback") {
                val principal: OAuthAccessTokenResponse.OAuth2? = call.principal()
                principal?.accessToken?.let {
                    val u = userRepository.updateUser(1, UserUpdate(stravaToken = it))
                }
                call.respond(" $principal, ${principal?.accessToken}")
            }
        }
    }
}