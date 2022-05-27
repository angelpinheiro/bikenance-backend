package com.bikenance.database.tables

import com.bikenance.database.UserDao
import com.bikenance.model.User
import com.bikenance.repository.UserRepository
import org.jetbrains.exposed.sql.Table


object Users : Table() {
    val id = integer("id").autoIncrement()
    val username = varchar("username", 20)
    val password = text("password")
    val stravaAthleteId = text("strava_athlete_id").nullable()
    val stravaToken = text("strava_token").nullable()
    override val primaryKey = PrimaryKey(id)
}

