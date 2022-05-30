package com.bikenance.features.strava.model

import com.bikenance.features.strava.StravaModel
import com.bikenance.model.User
import com.fasterxml.jackson.annotation.JsonProperty
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import org.litote.kmongo.Id
import org.litote.kmongo.id.toId
import java.util.*

data class StravaAthlete(
    @JsonProperty("id") var id: String,
    @JsonProperty("username") var username: String? = null,
    @JsonProperty("resource_state") var resourceState: Int? = null,
    @JsonProperty("firstname") var firstname: String? = null,
    @JsonProperty("lastname") var lastname: String? = null,
    @JsonProperty("city") var city: String? = null,
    @JsonProperty("state") var state: String? = null,
    @JsonProperty("country") var country: String? = null,
    @JsonProperty("sex") var sex: String? = null,
    @JsonProperty("premium") var premium: Boolean? = null,
    @JsonProperty("created_at") var createdAt: String? = null,
    @JsonProperty("updated_at") var updatedAt: String? = null,
    @JsonProperty("badge_type_id") var badgeTypeId: Int? = null,
    @JsonProperty("profile_medium") var profileMedium: String? = null,
    @JsonProperty("profile") var profile: String? = null,
    @JsonProperty("friend") var friend: String? = null,
    @JsonProperty("follower") var follower: String? = null,
    @JsonProperty("follower_count") var followerCount: Int? = null,
    @JsonProperty("friend_count") var friendCount: Int? = null,
    @JsonProperty("mutual_friend_count") var mutualFriendCount: Int? = null,
    @JsonProperty("athlete_type") var athleteType: Int? = null,
    @JsonProperty("date_preference") var datePreference: String? = null,
    @JsonProperty("measurement_preference") var measurementPreference: String? = null,
    @JsonProperty("ftp") var ftp: String? = null,
    @JsonProperty("weight") var weight: Int? = null,

    @BsonId
    val _id: Id<StravaAthlete> = ObjectId().toId()
    ) : StravaModel<StravaAthlete>()
