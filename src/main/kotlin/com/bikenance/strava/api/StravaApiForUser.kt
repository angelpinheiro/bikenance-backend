package com.bikenance.strava.api

import com.bikenance.strava.AuthData
import com.bikenance.strava.model.StravaActivity
import com.bikenance.strava.model.StravaAthlete
import com.bikenance.strava.model.StravaDetailedGear
import com.bikenance.strava.usecase.StravaTokenRefresh
import com.bikenance.login.config.AppConfig
import com.bikenance.repository.UserRepository
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import java.time.LocalDateTime
import java.time.ZoneOffset

val supportedActivityTypes = listOf("Ride", "EBikeRide", "VirtualRide")


object StravaApiEndpoints {
    const val athleteEndpoint = "https://www.strava.com/api/v3/athlete"
    const val activitiesEndpoint = "https://www.strava.com/api/v3/activities/?per_page=100"
    fun activityEndpoint(activityId: String) = "https://www.strava.com/api/v3/activities/$activityId"
    fun bikeEndpoint(id: String) = "https://www.strava.com/api/v3/gear/$id"
}

class Strava(private val client: HttpClient, val config: AppConfig, private val userRepository: UserRepository) {

    val refresher = StravaTokenRefresh(client, config.strava, userRepository)

    fun withAuth(auth: AuthData): com.bikenance.strava.api.StravaApiForUser {
        return com.bikenance.strava.api.StravaApiForUser(auth, this)
    }

    suspend fun athlete(auth: AuthData): StravaAthlete? {
        return authorizedGet(com.bikenance.strava.api.StravaApiEndpoints.athleteEndpoint, auth)
    }

    suspend fun activity(auth: AuthData, activityId: String): StravaActivity? {
        return authorizedGet(com.bikenance.strava.api.StravaApiEndpoints.activityEndpoint(activityId), auth)
    }

    suspend fun activities(auth: AuthData, from: LocalDateTime): List<StravaActivity>? {
        return authorizedGet(com.bikenance.strava.api.StravaApiEndpoints.activitiesEndpoint, auth) {
            parameter("after", from.toEpochSecond(ZoneOffset.UTC).toInt())
        }
    }

    suspend fun bike(auth: AuthData, id: String): StravaDetailedGear? {
        return authorizedGet(com.bikenance.strava.api.StravaApiEndpoints.bikeEndpoint(id), auth)
    }

    private suspend inline fun <reified T> authorizedGet(
        url: String,
        auth: AuthData,
        block: HttpRequestBuilder.() -> Unit = {}
    ): T? {
        val token = refresher.refreshAccessTokenIfNecessary(auth).accessToken
        val r = client.prepareGet(url) {
            headers["Authorization"] = "Bearer $token"
            block()
        }.execute()

        return if (r.status.isSuccess()) {
            com.bikenance.strava.api.mapper.readValue(r.bodyAsText())
        } else {
            null
        }
    }
}

val mapper: ObjectMapper = jacksonObjectMapper()
    .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)

class StravaApiForUser(private val auth: AuthData, private val strava: com.bikenance.strava.api.Strava) {
    suspend fun athlete(): StravaAthlete? = strava.athlete(auth)
    suspend fun activity(activityId: String): StravaActivity? = strava.activity(auth, activityId)
    suspend fun activities(from: LocalDateTime): List<StravaActivity>? = strava.activities(auth, from)
    suspend fun bike(id: String): StravaDetailedGear? = strava.bike(auth, id)
}