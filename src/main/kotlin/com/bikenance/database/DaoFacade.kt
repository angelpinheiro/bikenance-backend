package com.bikenance.database

import com.bikenance.model.User
import com.bikenance.model.UserUpdate

interface UserDaoFacade {
    suspend fun getById(id: String): User?
    suspend fun getByUsername(username: String): User?
    suspend fun getByAthleteId(athleteId: String): User?
    suspend fun findAll(): List<User>
    suspend fun filter(pattern: String): List<User>
    suspend fun create(title: String, body: String): User?
    suspend fun create(user: User): User?
    suspend fun update(id: String, user: User): Boolean
    suspend fun delete(id: String): Boolean
    suspend fun getByAccessToken(token: String): User?
}