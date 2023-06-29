package com.bikenance.model

import com.bikenance.database.mongodb.MongoModel
import com.bikenance.strava.AuthData

data class User(
    val username: String?,
    var password: String?,
    var athleteId: String? = null,
    var stravaAuthData: AuthData? = null,
    var firebaseToken: String? = null
) : MongoModel<User>()
data class UserUpdate(
    var username: String? = null,
    var password: String? = null,
    var athleteId: String? = null,
    var authData: String? = null
)


