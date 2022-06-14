package com.bikenance.model

import com.bikenance.database.mongodb.MongoModel
import com.bikenance.features.strava.AuthData
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import org.litote.kmongo.Id
import org.litote.kmongo.id.toId
import org.litote.kmongo.json

data class User(
    val username: String?,
    var password: String?,
    var athleteId: String? = null,
    var authData: AuthData? = null,
    var firebaseToken: String? = null
) : MongoModel<User>()
data class UserUpdate(
    var username: String? = null,
    var password: String? = null,
    var athleteId: String? = null,
    var authData: String? = null
)


