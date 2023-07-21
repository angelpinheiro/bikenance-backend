package com.bikenance.data.model

import com.bikenance.data.database.mongodb.MongoModel
import com.bikenance.data.model.components.BikeComponent
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime


sealed class BikeType(val name: String, val extendedType: String) {
    object Mtb : BikeType("MTB", "MTB")
    object Road : BikeType("Road", "Road Bike")
    object EBike : BikeType("E-Bike", "Electric Bike")
    object Gravel : BikeType("Gravel", "Gravel Bike")
    object Stationary : BikeType("Stationary", "Stationary Bike")
    object Unknown : BikeType("Unknown", "Unknown bike type")

    companion object {
        private val allKnownTypes by lazy {
            listOf(Mtb, Road, EBike, Gravel, Stationary)
        }

        private val allTypes by lazy {
            allKnownTypes.plus(Unknown)
        }

        fun getByName(name: String): BikeType = allTypes.find { it.name == name } ?: Unknown

        fun getAll() = allTypes

        fun getAllKnown() = allKnownTypes

    }
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
    @JsonProperty("full_suspension") val fullSuspension: Boolean = false,
    @JsonProperty("configDone") val configDone: Boolean = false,
    @JsonProperty("bike_type") var type: BikeType = BikeType.Unknown,
    @JsonProperty("components") val components: List<BikeComponent>? =null,
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


data class BikeUpdate(
    @JsonProperty("name") var name: String? = null,
    @JsonProperty("brand_name") var brandName: String? = null,
    @JsonProperty("model_name") var modelName: String? = null,
    @JsonProperty("photo_url") var photoUrl: String? = null,
    @JsonProperty("draft") var draft: Boolean = false,
    @JsonProperty("electric") val electric: Boolean = false,
    @JsonProperty("full_suspension") val fullSuspension: Boolean = false,
    @JsonProperty("configDone") val configDone: Boolean = false,
    @JsonProperty("bike_type") var type: BikeType = BikeType.Unknown,
)