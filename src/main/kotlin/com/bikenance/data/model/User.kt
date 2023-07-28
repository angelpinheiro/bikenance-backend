package com.bikenance.data.model

import com.bikenance.data.database.mongodb.MongoModel
import com.bikenance.api.strava.AuthData
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import org.litote.kmongo.Id
import org.litote.kmongo.id.toId


data class User(

    @BsonId
    override val _id: Id<User> = ObjectId().toId(),

    val username: String?,
    var password: String?,
    var athleteId: String? = null,
    var stravaAuthData: AuthData? = null,
    var firebaseToken: String? = null,
    var refreshToken: String? = null
) : MongoModel<User>()
data class UserUpdate(
    var username: String? = null,
    var password: String? = null,
    var athleteId: String? = null,
    var authData: String? = null
)


