package com.bikenance.features.login.usecase

import com.bikenance.database.UserDao
import com.bikenance.features.login.JwtConfig
import com.bikenance.features.login.JwtGenerator
import com.bikenance.features.login.data.LoginData

data class LoginResult(
    val success: Boolean,
    val token: String? = null,
    val message: String? = null
)

class LoginUseCase(config: JwtConfig) {

    // TODO: Inject
    private val userDao = UserDao()
    private val tokenGenerator = JwtGenerator(config)

    suspend fun loginUser(loginData: LoginData): LoginResult {
        val u = userDao.user(loginData.username)
        return if (u != null && u.password == loginData.password) {
            LoginResult(true, tokenGenerator.generateToken(loginData))
        } else {
            LoginResult(false, null, "Invalid credentials")
        }
    }

    suspend fun registerUser(user: LoginData): LoginResult {
        return if (userDao.user(user.username) == null) {
            val u = userDao.createUser(user.username, user.password)
            LoginResult(true, tokenGenerator.generateToken(user))
        } else {
            LoginResult(false, null, "Username not available")
        }
    }

}