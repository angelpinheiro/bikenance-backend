package com.bikenance.model

data class User(
    val id: Int,
    val username: String,
    var password: String,
    var athleteId: String? = null,
    var athleteToken: String? = null
)

data class UserUpdate(
    var username: String? = null,
    var password: String? = null,
    var athleteId: String? = null,
    var athleteToken: String? = null
)


