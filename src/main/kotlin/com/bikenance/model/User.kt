package com.bikenance.model

data class User(
    val id: Int,
    val username: String,
    val password: String,
    val stravaToken: String? = null,
    val stravaAthleteId: String? = null
)

data class UserUpdate(
    var username: String? = null,
    var password: String? = null,
    var stravaToken: String? = null,
    var stravaAthleteId: String? = null
)


