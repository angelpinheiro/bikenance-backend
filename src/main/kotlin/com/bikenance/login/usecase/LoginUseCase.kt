package com.bikenance.login.usecase

import com.bikenance.login.config.JwtMgr
import com.bikenance.login.data.LoginData
import com.bikenance.repository.UserRepository

data class LoginResult(
    val success: Boolean,
    val token: String? = null,
    val message: String? = null
)

class LoginUseCase(val jwt: JwtMgr, private val userRepository: UserRepository) {


    suspend fun loginUser(loginData: LoginData): LoginResult {
        val u = userRepository.getByUsername(loginData.username)
        return if (u != null && u.password == loginData.password) {
            LoginResult(true, jwt.generator.generateToken(u))
        } else {
            LoginResult(false, null, "Invalid credentials")
        }
    }

    suspend fun registerUser(user: LoginData): LoginResult {
        return if (userRepository.getByUsername(user.username) == null) {
            userRepository.create(user.username, user.password)?.let {
                LoginResult(true, jwt.generator.generateToken(it))
            } ?: LoginResult(false, null, "Sign up failed")

        } else {
            LoginResult(false, null, "Username not available")
        }
    }

}