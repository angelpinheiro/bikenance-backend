package com.bikenance.repository

import com.bikenance.database.UserDaoFacade
import com.bikenance.model.User

class UserRepository(private val userDao: UserDaoFacade) {

    suspend fun findById(id: Int) = userDao.user(id)

    suspend fun findByUsername(username: String) = userDao.user(username)

    suspend fun findAll() = userDao.allUsers()

    suspend fun search(pattern: String) = userDao.filter(pattern)

    suspend fun updateUser(id: Int, user: User): User? {
        if (userDao.editUser(id, user)) {
            return findById(id)
        }
        return null
    }

}