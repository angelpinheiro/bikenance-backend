package com.bikenance.data.model

import com.bikenance.data.database.mongodb.MongoModel
import com.bikenance.data.model.components.BikeComponent
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime


enum class BikeType(
    val type: String,
    val extendedType: String
) {
    MTB("MTB", "MTB Hardtail"),
    FULL_MTB("Full MTB", "MTB Full Suspension"),
    ROAD("Road", "Road Bike"),
    E_BIKE("E-Bike", "Electric Bike"),
    GRAVEL("Gravel", "Gravel Bike"),
    STATIONARY("Stationary", "Stationary Bike")
}

data class Bike(
    @JsonProperty("user_id") var userId: String? = null,
    @JsonProperty("strava_gear_id") var stravaId: String? = null,
    @JsonProperty("name") var name: String? = null,
    @JsonProperty("brand_name") var brandName: String? = null,
    @JsonProperty("model_name") var modelName: String? = null,
    @JsonProperty("distance") var distance: Long? = null,
    @JsonProperty("photo_url") var photoUrl: String? = null,
    @JsonProperty("draft") var draft: Boolean = false,
    @JsonProperty("electric") val electric: Boolean = false,
    @JsonProperty("configDone") val configDone: Boolean = false,
    @JsonProperty("bike_type") var type: BikeType = BikeType.MTB,
    @JsonProperty("components") val components: List<BikeComponent>? = emptyList(),
    @JsonProperty("stats") val stats: BikeStats? = null
) : MongoModel<Bike>()


data class BikeStats(
    @JsonProperty("ride_count")
    val rideCount: Double? = null,
    @JsonProperty("duration")
    val duration: Double = 0.0,
    @JsonProperty("distance")
    val distance: Double = 0.0,
    @JsonProperty("elevationGain")
    val elevationGain: Double = 0.0,
    @JsonProperty("average_speed")
    val averageSpeed: Double? = null,
    @JsonProperty("max_speed")
    val maxSpeed: Double? = null,
    @JsonProperty("last_ride_date")
    val lastRideDate: LocalDateTime? = null,
)

data class SyncBikesData(
    @JsonProperty("syncData") val syncData: Map<String, Boolean>
)
