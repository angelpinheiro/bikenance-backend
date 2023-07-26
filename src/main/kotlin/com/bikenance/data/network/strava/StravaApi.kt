package com.bikenance.data.network.strava

import com.bikenance.AppConfig
import com.bikenance.api.strava.AuthData
import com.bikenance.data.model.strava.AthleteStats
import com.bikenance.data.model.strava.StravaActivity
import com.bikenance.data.model.strava.StravaAthlete
import com.bikenance.data.model.strava.StravaDetailedGear
import com.bikenance.usecase.strava.StravaTokenRefresh
import com.bikenance.usecase.strava.TokenRefresher
import com.bikenance.util.bknLogger
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

class StravaApi(
    private val client: HttpClient,
    private val refresher: TokenRefresher,
    private val mapper: ObjectMapper
) {

    val log = bknLogger("Strava")

    fun withAuth(auth: AuthData): StravaApiForUser {
        return StravaApiForUser(auth, this)
    }

    suspend fun athlete(auth: AuthData): StravaApiResponse<StravaAthlete> {
        return authorizedGetResponse(StravaApiEndpoints.athleteEndpoint, auth)
    }

    suspend fun athleteStats(auth: AuthData, athleteId: String): StravaApiResponse<AthleteStats> {
        return authorizedGetResponse(StravaApiEndpoints.athleteStatsEndpoint(athleteId), auth)
    }

    suspend fun activity(auth: AuthData, activityId: String): StravaApiResponse<StravaActivity> {
        return authorizedGetResponse(StravaApiEndpoints.activityEndpoint(activityId), auth)
    }

    suspend fun activities(auth: AuthData, from: LocalDateTime): StravaApiResponse<List<StravaActivity>> {
        return authorizedGetResponse(StravaApiEndpoints.activitiesEndpoint, auth) {
            parameter("after", from.toEpochSecond(ZoneOffset.UTC).toInt())
        }
    }

    suspend fun bike(auth: AuthData, id: String): StravaApiResponse<StravaDetailedGear> {
        return authorizedGetResponse(StravaApiEndpoints.bikeEndpoint(id), auth)
    }

    suspend fun activitiesPaginated(
        auth: AuthData,
        page: Int,
        perPage: Int = 30,
        after: LocalDateTime?
    ): StravaApiResponse<List<StravaActivity>> {
        return authorizedGetResponse(StravaApiEndpoints.activitiesPaginatedEndpoint, auth) {
            parameter("page", page)
            parameter("per_page", perPage)
            after?.let {
                parameter("after", after.atZone(ZoneId.systemDefault()).toEpochSecond())
            }
        }
    }



    private suspend inline fun <reified T> authorizedGetResponse(
        url: String,
        auth: AuthData,
        block: HttpRequestBuilder.() -> Unit = {}
    ): StravaApiResponse<T> {

        val token = refresher.refreshAccessTokenIfNecessary(auth).accessToken

        return try {
            log.info("Strava authorizedGet to [$url]")
            val r = client.prepareGet(url) {
                headers["Authorization"] = "Bearer $token"
                block()
            }.execute()
            log.info("Strava authorizedGet response status [${r.status}]")

            if (r.status.isSuccess()) {
                val data = r.bodyAsText()
                log.error("Strava API success [${r.status}] <- [$url] ")
                StravaApiResponse.Success(mapper.readValue<T>(data))
            } else {
                val error = r.bodyAsText()
                val fault = mapper.readValue<StravaApiFault>(error)
                log.error("Strava API error [${r.status}] <- [$url] ")
                StravaApiResponse.Fault(r.status, fault)
            }

        } catch (e: Exception) {
            log.error("Strava API Exception", e)
            throw e
//            StravaApiResponse.Fault(HttpStatusCode.e)
        }
    }
//
//    private suspend inline fun <reified T> authorizedGet(
//        url: String,
//        auth: AuthData,
//        block: HttpRequestBuilder.() -> Unit = {}
//    ): T? {
//        val token = refresher.refreshAccessTokenIfNecessary(auth).accessToken
//
//        log.info("Strava authorizedGet to [$url]")
//        return try {
//            val r = client.prepareGet(url) {
//                headers["Authorization"] = "Bearer $token"
//                block()
//            }.execute()
//
//            log.info("Strava authorizedGet response status [${r.status}]")
//
//            if (r.status.isSuccess()) {
//                mapper.readValue(r.bodyAsText())
//            } else {
//                null
//            }
//
//        } catch (e: Exception) {
//            log.error("Strava API authorizedGet exception", e)
//            null
//        }
//    }
}