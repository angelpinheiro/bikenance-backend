package com.bikenance.repository

import com.bikenance.database.UserDaoFacade
import com.bikenance.model.User

class UserRepository(private val userDao: UserDaoFacade) {

    suspend fun create(user: User) = userDao.createUser(user)

    suspend fun create(username: String, password: String) = userDao.createUser(username,password)

    suspend fun findById(id: String) = userDao.user(id)

    suspend fun findByToken(token: String) = userDao.findByToken(token)

    suspend fun findByAthleteId(athleteId: String) = userDao.findByAthleteId(athleteId)

    suspend fun findByUsername(username: String) = userDao.user(username)

    suspend fun findAll() = userDao.allUsers()

    suspend fun search(pattern: String) = userDao.filter(pattern)

    suspend fun updateUser(id: String, user: User): User? {
        if (userDao.updateUser(id, user)) {
            return findById(id)
        }
        return null
    }

}