package com.bikenance.features.strava

import com.bikenance.database.mongodb.DB
import com.bikenance.features.strava.api.Strava
import com.bikenance.features.strava.model.StravaAthlete
import com.bikenance.features.strava.usecase.handleOAuthCallback
import com.bikenance.model.AthleteVO
import com.bikenance.repository.UserRepository
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.logging.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.pipeline.*
import org.koin.ktor.ext.inject


fun Application.configureOAuth(config: StravaConfig) {

    val strava: Strava by inject()
    val db: DB by inject()
    val userRepository: UserRepository by inject()

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

        authenticate("auth-oauth-strava") {
            get("/strava") {
                call.respondRedirect("${config.apiUrl}/callback")
            }

            get("/callback") {

                val token = getAccessToken()
                val athlete = getAthleteParameter()?.reSerialize<AthleteVO>()

                if (token != null && athlete != null) {
                    handleOAuthCallback(strava, db, userRepository, token)
                }
                call.respond("$athlete")
            }
        }
    }
}


fun PipelineContext<*, ApplicationCall>.getAccessToken(): String? {
    val principal: OAuthAccessTokenResponse.OAuth2? = call.principal()
    return principal?.accessToken
}

fun PipelineContext<*, ApplicationCall>.getAthleteParameter(): StravaAthlete? {
    val principal: OAuthAccessTokenResponse.OAuth2? = call.principal()
    return principal?.extraParameters?.get("athlete")?.let {
        return@let mapper.readValue<StravaAthlete>(it)
    }
}