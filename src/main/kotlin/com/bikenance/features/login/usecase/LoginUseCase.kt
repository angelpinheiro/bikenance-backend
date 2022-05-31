package com.bikenance.features.login.usecase

import com.bikenance.features.login.JwtConfig
import com.bikenance.features.login.JwtGenerator
import com.bikenance.features.login.data.LoginData
import com.bikenance.repository.UserRepository

data class LoginResult(
    val success: Boolean,
    val token: String? = null,
    val message: String? = null
)

class LoginUseCase(config: JwtConfig, private val userRepository: UserRepository) {

    private val tokenGenerator = JwtGenerator(config)

    suspend fun loginUser(loginData: LoginData): LoginResult {
        val u = userRepository.getByUsername(loginData.username)
        return if (u != null && u.password == loginData.password) {
            LoginResult(true, tokenGenerator.generateToken(loginData))
        } else {
            LoginResult(false, null, "Invalid credentials")
        }
    }

    suspend fun registerUser(user: LoginData): LoginResult {
        return if (userRepository.getByUsername(user.username) == null) {
            val u = userRepository.create(user.username, user.password)
            LoginResult(true, tokenGenerator.generateToken(user))
        } else {
            LoginResult(false, null, "Username not available")
        }
    }

}