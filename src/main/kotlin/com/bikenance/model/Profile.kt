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


data class SetupProfileUpdate(
    @JsonProperty("firstName") val firstName: String,
    @JsonProperty("lastName") val lastName: String,
    @JsonProperty("profilePhotoUrl") val profilePhotoUrl: String?,
    @JsonProperty("synchronizedBikesIds") val synchronizedBikesIds: List<String>
)
