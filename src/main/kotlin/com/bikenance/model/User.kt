package com.bikenance.model

import com.bikenance.features.strava.model.StravaActivity
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import org.litote.kmongo.Id
import org.litote.kmongo.id.toId
import java.util.UUID

data class User(
    val id: Int,
    val username: String,
    var password: String,
    var athleteId: String? = null,
    var athleteToken: String? = null,
)

data class UserUpdate(
    var username: String? = null,
    var password: String? = null,
    var athleteId: String? = null,
    var athleteToken: String? = null
)


