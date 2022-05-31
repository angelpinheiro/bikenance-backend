package com.bikenance.model

import com.bikenance.features.strava.AuthData
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import org.litote.kmongo.Id
import org.litote.kmongo.id.toId

data class User(
    val username: String,
    var password: String,
    var athleteId: String? = null,
    var authData: AuthData? = null,

    @BsonId
    val _id: Id<User> = ObjectId().toId()
)

data class UserUpdate(
    var username: String? = null,
    var password: String? = null,
    var athleteId: String? = null,
    var authData: String? = null
)


