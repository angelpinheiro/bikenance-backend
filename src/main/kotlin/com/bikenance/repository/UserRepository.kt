package com.bikenance.repository

import com.bikenance.database.UserDao
import com.bikenance.features.login.JwtConfig
import com.bikenance.features.login.JwtGenerator
import com.bikenance.features.login.data.LoginData
import com.bikenance.model.User

class UserRepository() {

    // TODO: Inject
    private val userDao = UserDao()

    suspend fun findById(id: Int) = userDao.user(id)

    suspend fun findByUsername(username: String) = userDao.user(username)

    suspend fun findAll() = userDao.allUsers()

    suspend fun updateUser(id: Int, user: User): User? {
        if(userDao.editUser(id, user)){
            return findById(id)
        }
        return null
    }

}