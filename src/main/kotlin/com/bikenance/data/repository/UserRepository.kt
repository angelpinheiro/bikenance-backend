package com.bikenance.data.repository

import com.bikenance.data.database.UserDao
import com.bikenance.data.model.User
import com.bikenance.data.model.UserUpdate

class UserRepository(private val userDao: UserDao) {

    suspend fun create(user: User) = userDao.create(user)

    suspend fun create(username: String, password: String) = userDao.create(username, password)

    suspend fun getById(id: String) = userDao.getById(id)

    suspend fun getByToken(token: String) = userDao.getByAccessToken(token)

    suspend fun getByAthleteId(athleteId: String) = userDao.getByAthleteId(athleteId)

    suspend fun getByUsername(username: String) = userDao.getByUsername(username)

    suspend fun findAll() = userDao.findAll()

    suspend fun filter(pattern: String) = userDao.filter(pattern)

    suspend fun update(id: String, user: UserUpdate): User? {
        if (userDao.update(id, user)) {
            return getById(id)
        }
        return null
    }

    suspend fun update(id: String, user: User): User? {
        if (userDao.update(id, user)) {
            return getById(id)
        }
        return null
    }

}