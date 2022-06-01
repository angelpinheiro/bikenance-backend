package com.bikenance.features.strava.api

import com.bikenance.features.login.config.AppConfig
import com.bikenance.features.strava.AuthData
import com.bikenance.features.strava.model.StravaActivity
import com.bikenance.features.strava.model.StravaAthlete
import com.bikenance.features.strava.model.StravaDetailedGear
import com.bikenance.features.strava.usecase.StravaTokenRefresh
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
    const val activitiesEndpoint = "https://www.strava.com/api/v3/activities/"
    fun activityEndpoint(activityId: String) = "https://www.strava.com/api/v3/activities/$activityId"
    fun bikeEndpoint(id: String) = "https://www.strava.com/api/v3/gear/$id"
}

class Strava(private val client: HttpClient, val config: AppConfig, private val userRepository: UserRepository) {

    val refresher = StravaTokenRefresh(client, config.strava, userRepository)

    fun withAuth(auth: AuthData): StravaApiForUser {
        return StravaApiForUser(auth, this)
    }

    suspend fun athlete(auth: AuthData): StravaAthlete? {
        return authorizedGet(StravaApiEndpoints.athleteEndpoint, auth)
    }

    suspend fun activity(auth: AuthData, activityId: String): StravaActivity? {
        return authorizedGet(StravaApiEndpoints.activityEndpoint(activityId), auth)
    }

    suspend fun activities(auth: AuthData, from: LocalDateTime): List<StravaActivity>? {
        return authorizedGet(StravaApiEndpoints.activitiesEndpoint, auth) {
            parameter("after", from.toEpochSecond(ZoneOffset.UTC).toInt())
        }
    }

    suspend fun bike(auth: AuthData, id: String): StravaDetailedGear? {
        return authorizedGet(StravaApiEndpoints.bikeEndpoint(id), auth)
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
            mapper.readValue(r.bodyAsText())
        } else {
            null
        }
    }
}

val mapper: ObjectMapper = jacksonObjectMapper()
    .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)

class StravaApiForUser(private val auth: AuthData, private val strava: Strava) {
    suspend fun athlete(): StravaAthlete? = strava.athlete(auth)
    suspend fun activity(activityId: String): StravaActivity? = strava.activity(auth, activityId)
    suspend fun activities(from: LocalDateTime): List<StravaActivity>? = strava.activities(auth, from)
    suspend fun bike(id: String): StravaDetailedGear? = strava.bike(auth, id)
}