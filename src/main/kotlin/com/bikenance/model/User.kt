package com.bikenance.model

import org.jetbrains.exposed.sql.Table

data class User(
    val id: Int,
    val username: String,
    val password: String,
    val stravaToken: String? = null,
    val stravaAthleteId: String? = null
)

data class UserUpdate(
    val username: String? = null,
    val password: String? = null,
    val stravaToken: String? = null,
    val stravaAthleteId: String? = null
)