package com.bikenance.model

import com.bikenance.database.mongodb.MongoModel
import com.bikenance.features.strava.model.StravaActivity
import com.fasterxml.jackson.annotation.JsonProperty

data class ExtendedProfile(
    val profile: Profile?,
    val bikes: List<Bike>? = null,
    val rides: List<BikeRide>? = null
)

data class Profile(
    @JsonProperty("user_id") var userId: String? = null,
    @JsonProperty("username") var username: String? = null,
    @JsonProperty("firstname") var firstname: String? = null,
    @JsonProperty("lastname") var lastname: String? = null,
    @JsonProperty("profile_photo_url") var profilePhotoUrl: String? = null,
    @JsonProperty("sex") var sex: String? = null,
    @JsonProperty("weight") var weight: Int? = null,
    @JsonProperty("created_at") var createdAt: String? = null,
) : MongoModel<Profile>()

data class Bike(
    @JsonProperty("user_id") var userId: String? = null,
    @JsonProperty("strava_gear_id") var stravaId: String? = null,
    @JsonProperty("name") var name: String? = null,
    @JsonProperty("brand_name") var brandName: String? = null,
    @JsonProperty("model_name") var modelName: String? = null,
    @JsonProperty("distance") var distance: Long? = null,
    @JsonProperty("photo_url") var photoUrl: String? = null,
    @JsonProperty("draft") var draft: Boolean = false,
    @JsonProperty("bike_type") var type: String = "MTB", // mtb | road | electric | gravel | ...
    @JsonProperty("current_year_distance") var currentYearDistance: Int? = null,
    @JsonProperty("current_month_distance") var currentMonthDistance: Int? = null,
) : MongoModel<Bike>()

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


data class SetupProfileUpdate(
    @JsonProperty("firstName") val firstName: String,
    @JsonProperty("lastName") val lastName: String,
    @JsonProperty("profilePhotoUrl") val profilePhotoUrl: String?,
    @JsonProperty("synchronizedBikesIds") val synchronizedBikesIds: List<String>
)

data class SyncBikes(
    @JsonProperty("synchronizedBikesIds") val synchronizedBikesIds: List<String>
)

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