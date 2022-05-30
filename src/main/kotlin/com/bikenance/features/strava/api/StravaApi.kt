package com.bikenance.features.strava.api

import com.bikenance.features.strava.model.StravaActivity
import com.bikenance.features.strava.model.StravaAthlete
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset


object StravaApiEndpoints {
    const val athleteEndpoint = "https://www.strava.com/api/v3/athlete"
    fun activityEndpoint(activityId: String) = "https://www.strava.com/api/v3/activities/$activityId"

    val activitiesEndpoint = "https://www.strava.com/api/v3/activities/"
}

class Strava(private val client: HttpClient) {



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

        return mapper.readValue(response.bodyAsText())
    }
}


val mapper: ObjectMapper = jacksonObjectMapper()
    .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)

class StravaApi(private val token: String, private val functions: Strava) {
    suspend fun athlete(): StravaAthlete = functions.athlete(token)
    suspend fun activity(activityId: String): StravaActivity = functions.activity(token, activityId)

    suspend fun activities(from: LocalDateTime): List<StravaActivity> = functions.activities(token, from)
}