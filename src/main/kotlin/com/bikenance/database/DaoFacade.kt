package com.bikenance.database

import com.bikenance.model.User

interface UserDaoFacade {
    suspend fun user(id: Int): User?
    suspend fun user(username: String): User?
    suspend fun allUsers(): List<User>
    suspend fun createUser(title: String, body: String): User?
    suspend fun editUser(id: Int, user: User): Boolean
    suspend fun deleteUser(id: Int): Boolean
}