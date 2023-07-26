package com.bikenance.data.network.strava

object StravaApiEndpoints {
    const val athleteEndpoint = "https://www.strava.com/api/v3/athlete"
    const val activitiesEndpoint = "https://www.strava.com/api/v3/activities/?per_page=100"
    const val activitiesPaginatedEndpoint = "https://www.strava.com/api/v3/activities"
    fun activityEndpoint(activityId: String) = "https://www.strava.com/api/v3/activities/$activityId"
    fun bikeEndpoint(id: String) = "https://www.strava.com/api/v3/gear/$id"
    fun athleteStatsEndpoint(id: String) = "https://www.strava.com/api/v3/athletes/$id/stats"
}