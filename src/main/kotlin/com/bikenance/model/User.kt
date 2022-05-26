package com.bikenance.model

import org.jetbrains.exposed.sql.Table

data class User(
    val id: Int,
    val username: String,
    val password: String
)

object Users : Table() {
    val id = integer("id").autoIncrement()
    val username = varchar("username", 20)
    val password = text("password")
    override val primaryKey = PrimaryKey(id)
}