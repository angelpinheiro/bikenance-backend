package com.bikenance.features.strava.api

import com.bikenance.features.login.config.AppConfig
import com.bikenance.features.strava.AuthData
import com.bikenance.features.strava.StravaOAuthEndpoints
import com.bikenance.features.strava.model.StravaActivity
import com.bikenance.features.strava.model.StravaAthlete
import com.bikenance.features.strava.model.StravaDetailedGear
import com.bikenance.features.strava.model.StravaRequestParams
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import java.time.LocalDateTime
import java.time.ZoneOffset

val supportedActivityTypes = listOf("Ride", "EBikeRide", "VirtualRide")

data class RefreshTokenResponse(
    @JsonProperty("token_type") var tokenType: String? = null,
    @JsonProperty("expires_at") var expiresAt: Int? = null,
    @JsonProperty("expires_in") var expiresIn: Int? = null,
    @JsonProperty("refresh_token") var refreshToken: String? = null,
    @JsonProperty("access_token") var accessToken: String? = null,
)

object StravaApiEndpoints {
    const val athleteEndpoint = "https://www.strava.com/api/v3/athlete"
    const val activitiesEndpoint = "https://www.strava.com/api/v3/activities/"
    fun activityEndpoint(activityId: String) = "https://www.strava.com/api/v3/activities/$activityId"
    fun bikeEndpoint(id: String) =  "https://www.strava.com/api/v3/gear/$id"
}

class Strava(private val client: HttpClient, val config: AppConfig) {

    fun withToken(token: String): StravaApi {
        return StravaApi(token, this)
    }

    suspend fun athlete(token: String): StravaAthlete {
        val response = client.get(StravaApiEndpoints.athleteEndpoint) {
            headers["Authorization"] = "Bearer $token"
        }

        return mapper.readValue(response.bodyAsText())
    }

    suspend fun activity(token: String, activityId: String): StravaActivity {
        val response = client.get(StravaApiEndpoints.activityEndpoint(activityId)) {
            headers["Authorization"] = "Bearer $token"
        }

        return mapper.readValue(response.bodyAsText())
    }

    suspend fun activities(token: String, from: LocalDateTime): List<StravaActivity> {
        val response = client.get(StravaApiEndpoints.activitiesEndpoint) {
            headers["Authorization"] = "Bearer $token"
            parameter("after ", from.toEpochSecond(ZoneOffset.UTC).toInt())
        }
        return mapper.readValue<List<StravaActivity>>(response.bodyAsText()).filter {
            supportedActivityTypes.contains(it.type)
        }
    }

    suspend fun bike(token: String, id: String): StravaDetailedGear {
        val response = client.get(StravaApiEndpoints.bikeEndpoint(id)) {
            headers["Authorization"] = "Bearer $token"
        }
        return mapper.readValue(response.bodyAsText())
    }

    suspend fun refreshAccessToken(refreshToken: String) : RefreshTokenResponse {
        val response = client.post(StravaOAuthEndpoints.accessTokenUrl) {
            parameter(StravaRequestParams.CLIENT_ID, config.strava.clientId)
            parameter(StravaRequestParams.CLIENT_SECRET, config.strava.clientSecret)
            parameter(StravaRequestParams.GRANT_TYPE, "refresh_token")
            parameter(StravaRequestParams.REFRESH_TOKEN, refreshToken)
        }
        return mapper.readValue(response.bodyAsText())
    }
}

val mapper: ObjectMapper = jacksonObjectMapper()
    .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)

class StravaApi(private val token: String, private val functions: Strava) {
    suspend fun athlete(): StravaAthlete = functions.athlete(token)
    suspend fun activity(activityId: String): StravaActivity = functions.activity(token, activityId)
    suspend fun activities(from: LocalDateTime): List<StravaActivity> = functions.activities(token, from)
    suspend fun bike(id: String): StravaDetailedGear = functions.bike(token, id)
}