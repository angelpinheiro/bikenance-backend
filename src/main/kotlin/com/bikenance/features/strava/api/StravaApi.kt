package com.bikenance.features.strava.api

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job


val scope = CoroutineScope(Job() + Dispatchers.IO)

val client = HttpClient(CIO) {
    install(Logging)
}

object StravaApiEndpoints {
    const val ATHLETE = "https://www.strava.com/api/v3/athlete"
}

class StravaApi() {

    suspend fun athlete(token: String): String {
        val response = client.get(StravaApiEndpoints.ATHLETE) {
            headers["Authorization"] = "Bearer $token"
        }

        return if (response.status == HttpStatusCode.OK) {
            response.bodyAsText()
        } else {
            "FAIL"
        }
    }

}