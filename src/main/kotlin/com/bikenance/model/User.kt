package com.bikenance.model

import org.jetbrains.exposed.sql.Table

data class User(
    val id: Int,
    val username: String,
    val password: String,
    val stravaToken: String? = null
)

data class UserUpdate(
    val username: String? = null,
    val password: String? = null,
    val stravaToken: String? = null
)


object Users : Table() {
    val id = integer("id").autoIncrement()
    val username = varchar("username", 20)
    val password = text("password")
    val stravaToken = text("strava_token").nullable()
    override val primaryKey = PrimaryKey(id)
}