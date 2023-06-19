package com.bikenance.model

import com.bikenance.database.mongodb.MongoModel
import com.fasterxml.jackson.annotation.JsonProperty

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


data class SyncBikes(
    @JsonProperty("synchronizedBikesIds") val synchronizedBikesIds: List<String>
)
