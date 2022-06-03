package com.bikenance.model

import com.fasterxml.jackson.annotation.JsonProperty
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import org.litote.kmongo.Id
import org.litote.kmongo.id.toId

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
)

class Bike(
    @BsonId val _id: Id<User> = ObjectId().toId(),
    @JsonProperty("user_id") var userId: String? = null,
    @JsonProperty("strava_gear_id") var stravaId: String? = null,
    @JsonProperty("name") var name: String? = null,
    @JsonProperty("brand_name") var brandName: String? = null,
    @JsonProperty("model_name") var modelName: String? = null,
    @JsonProperty("distance") var distance: Int? = null,
)

class BikeRide(
    @BsonId val _id: Id<User> = ObjectId().toId(),
    @JsonProperty("user_id") var userId: String? = null,
    @JsonProperty("bike_id") var bikeId: String? = null,
    @JsonProperty("strava_activity_id") var stravaId: String? = null,
    @JsonProperty("name") var name: String? = null,
    @JsonProperty("distance") var distance: Int? = null,
    @JsonProperty("moving_time") var movingTime: Int? = null,
    @JsonProperty("elapsed_time") var elapsedTime: Int? = null,
    @JsonProperty("total_elevation_gain") var totalElevationGain: Int? = null,
    @JsonProperty("date_time") var dateTime: String? = null
)