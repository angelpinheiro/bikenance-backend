package com.bikenance.features.strava.api

import com.bikenance.features.strava.model.StravaAthlete
import com.bikenance.model.AthleteVO

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue


val scope = CoroutineScope(Job() + Dispatchers.IO)

val client = HttpClient(CIO) {
    install(Logging)
}

object StravaApiEndpoints {
    const val ATHLETE = "https://www.strava.com/api/v3/athlete"
}

val mapper: ObjectMapper = jacksonObjectMapper()
    .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)

class StravaApi() {

    suspend fun athlete(token: String): StravaAthlete {
        val response = client.get(StravaApiEndpoints.ATHLETE) {
            headers["Authorization"] = "Bearer $token"
        }
        val athlete : StravaAthlete = mapper.readValue(response.bodyAsText())
        println(athlete)
        return athlete
    }

}