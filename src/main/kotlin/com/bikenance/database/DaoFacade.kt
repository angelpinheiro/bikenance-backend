package com.bikenance.database

import com.bikenance.model.User
import com.bikenance.model.UserUpdate

interface UserDaoFacade {
    suspend fun user(id: Int): User?
    suspend fun user(username: String): User?
    suspend fun findByAthleteId(athleteId: String): User?
    suspend fun allUsers(): List<User>
    suspend fun filter(pattern: String): List<User>
    suspend fun createUser(title: String, body: String): User?
    suspend fun createUser(user: User): User?
    suspend fun editUser(id: Int, user: UserUpdate): Boolean
    suspend fun deleteUser(id: Int): Boolean
}