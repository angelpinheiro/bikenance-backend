package com.bikenance.database.tables

import org.jetbrains.exposed.sql.Table


object Users : Table() {
    val id = integer("id").autoIncrement()
    val username = varchar("username", 20)
    val password = text("password")
    val athleteId = text("athlete_id").nullable()
    val athleteToken = text("athlete_token").nullable()
    override val primaryKey = PrimaryKey(id)
}

