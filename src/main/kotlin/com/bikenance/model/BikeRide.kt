package com.bikenance.model

import com.bikenance.database.mongodb.MongoModel
import com.bikenance.features.strava.model.StravaActivity
import com.fasterxml.jackson.annotation.JsonProperty

data class BikeRide(
    @JsonProperty("user_id") var userId: String? = null,
    @JsonProperty("bike_id") var bikeId: String? = null,
    @JsonProperty("strava_activity_id") var stravaId: String? = null,
    @JsonProperty("name") var name: String? = null,
    @JsonProperty("distance") var distance: Long? = null,
    @JsonProperty("moving_time") var movingTime: Int? = null,
    @JsonProperty("elapsed_time") var elapsedTime: Int? = null,
    @JsonProperty("total_elevation_gain") var totalElevationGain: Int? = null,
    @JsonProperty("date_time") var dateTime: String? = null,
    @JsonProperty("map_summary_polyline") var mapSummaryPolyline: String? = null,
) : MongoModel<BikeRide>()


fun StravaActivity.toBikeRide(user: User, bike: Bike?): BikeRide {
    return BikeRide(
        userId = user.oid(),
        stravaId = id,
        bikeId = bike?.oid(),
        name = name,
        distance = distance,
        movingTime = movingTime,
        elapsedTime = elapsedTime,
        dateTime = startDate,
        totalElevationGain = totalElevationGain,
        mapSummaryPolyline = map?.summaryPolyline
    )
}