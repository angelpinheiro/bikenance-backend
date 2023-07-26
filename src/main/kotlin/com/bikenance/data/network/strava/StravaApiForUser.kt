package com.bikenance.data.network.strava

import com.bikenance.api.strava.AuthData
import com.bikenance.data.model.strava.AthleteStats
import com.bikenance.data.model.strava.StravaActivity
import com.bikenance.data.model.strava.StravaAthlete
import com.bikenance.data.model.strava.StravaDetailedGear
import java.time.LocalDateTime

val supportedActivityTypes = listOf("Ride", "EBikeRide", "VirtualRide")

class StravaApiForUser(private val auth: AuthData, private val strava: StravaApi) {
    suspend fun athlete(): StravaApiResponse<StravaAthlete> = strava.athlete(auth)
    suspend fun athleteStats(athleteId: String): StravaApiResponse<AthleteStats> = strava.athleteStats(auth, athleteId)
    suspend fun bike(id: String): StravaApiResponse<StravaDetailedGear> = strava.bike(auth, id)
    suspend fun activity(activityId: String): StravaApiResponse<StravaActivity> = strava.activity(auth, activityId)
    suspend fun activitiesPaginated(
        page: Int, perPage: Int = 100, after: LocalDateTime? = null
    ): StravaApiResponse<List<StravaActivity>> = strava.activitiesPaginated(auth, page, perPage, after)

}