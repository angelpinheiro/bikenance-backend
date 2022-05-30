package com.bikenance.features.strava.routing

import com.bikenance.database.mongodb.DB
import com.bikenance.features.strava.api.Strava
import com.bikenance.features.strava.model.StravaAthlete
import com.bikenance.repository.UserRepository
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.server.response.*

fun Application.stravaLogin() {

    val db: DB by inject()
    val userRepository: UserRepository by inject()
    val strava: Strava by inject()
    val client: HttpClient by inject()
    val mapper: ObjectMapper by inject()

    val clientId = environment.config.property("strava_webhooks.client_id").getString()
    val clientSecret = environment.config.property("strava_webhooks.client_secret").getString()


    routing {

        get("strava/login") {
            call.respond("POST please")
        }

        post("/strava/token") {
            val code = call.parameters["code"]

            val result = client.post("https://www.strava.com/oauth/token") {
                parameter(ReqParams.CLIENT_ID, clientId)
                parameter(ReqParams.CLIENT_SECRET, clientSecret)
            }

            val tokenResponse : TokenResponse = mapper.readValue(result.bodyAsText())
            call.respond(tokenResponse.accessToken ?: "Fail")
        }
    }
}

data class TokenResponse(
    @JsonProperty("token_type") var tokenType: String? = null,
    @JsonProperty("expires_at") var expiresAt: Int? = null,
    @JsonProperty("expires_in") var expiresIn: Int? = null,
    @JsonProperty("refresh_token") var refreshToken: String? = null,
    @JsonProperty("access_token") var accessToken: String? = null,
    @JsonProperty("athlete") var athlete: StravaAthlete? = null
)