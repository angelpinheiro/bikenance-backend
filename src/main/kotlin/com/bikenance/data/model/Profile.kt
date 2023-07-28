package com.bikenance.data.model

import com.bikenance.data.database.mongodb.MongoModel
import com.bikenance.data.model.strava.AthleteStats
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

    @BsonId
    override val _id: Id<Profile> = ObjectId().toId(),

    @JsonProperty("user_id") var userId: String? = null,
    @JsonProperty("username") var username: String? = null,
    @JsonProperty("firstname") var firstname: String? = null,
    @JsonProperty("lastname") var lastname: String? = null,
    @JsonProperty("profile_photo_url") var profilePhotoUrl: String? = null,
    @JsonProperty("sex") var sex: String? = null,
    @JsonProperty("weight") var weight: Int? = null,
    @JsonProperty("created_at") var createdAt: String? = null,
    @JsonProperty("stats") val athleteStats: AthleteStats? = null,
    @JsonProperty("sync") val sync: Boolean = false
    ) : MongoModel<Profile>()


data class SetupProfileUpdate(
    @JsonProperty("firstName") val firstName: String,
    @JsonProperty("lastName") val lastName: String,
    @JsonProperty("profilePhotoUrl") val profilePhotoUrl: String?,
    @JsonProperty("synchronizedBikesIds") val synchronizedBikesIds: List<String>
)
