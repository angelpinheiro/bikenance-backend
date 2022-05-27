package com.bikenance.model

import com.bikenance.database.tables.AthleteEntity
import com.fasterxml.jackson.annotation.JsonProperty

data class AthleteVO(
    @JsonProperty("id") var id: String? = null,
    @JsonProperty("username") var username: String? = null,
    @JsonProperty("firstname") var firstname: String? = null,
    @JsonProperty("lastname") var lastname: String? = null,
    @JsonProperty("city") var city: String? = null,
    @JsonProperty("state") var state: String? = null,
    @JsonProperty("country") var country: String? = null,
    @JsonProperty("sex") var sex: String? = null,
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
    @JsonProperty("weight") var weight: Int? = null
)
