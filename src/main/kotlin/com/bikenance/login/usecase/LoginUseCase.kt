package com.bikenance.login.usecase

import com.bikenance.login.config.JwtMgr
import com.bikenance.login.model.LoginData
import com.bikenance.login.model.RefreshData
import com.bikenance.repository.UserRepository

data class LoginResult(
    val success: Boolean,
    val token: String? = null,
    val refreshToken: String? = null,
    val message: String? = null
)

class LoginUseCase(val jwt: JwtMgr, private val userRepository: UserRepository) {

    suspend fun refreshUserTokens(refreshData: RefreshData): LoginResult {

        val decodedJWT = jwt.verifier.verifier.verify(refreshData.refreshToken)
        val u = userRepository.getById(decodedJWT.subject)
        return if (u != null && u.refreshToken == refreshData.refreshToken) {
            val tokens = jwt.generator.generateTokenPair(u)
            u.refreshToken = tokens.refreshToken
            userRepository.update(u.oid(), u)
            LoginResult(true, tokens.token, tokens.refreshToken)
        } else {
            LoginResult(false, null, "Invalid credentials")
        }
    }


    suspend fun loginUser(loginData: LoginData): LoginResult {
        val u = userRepository.getByUsername(loginData.username)
        return if (u != null && u.password == loginData.password) {
            val tokens = jwt.generator.generateTokenPair(u)
            u.refreshToken = tokens.refreshToken
            userRepository.update(u.oid(), u)
            LoginResult(true, tokens.token, tokens.refreshToken)
        } else {
            LoginResult(false, null, "Invalid credentials")
        }
    }

    suspend fun registerUser(user: LoginData): LoginResult {
        return if (userRepository.getByUsername(user.username) == null) {


            userRepository.create(user.username, user.password)?.let {

                val tokens = jwt.generator.generateTokenPair(it)
                it.refreshToken = tokens.refreshToken
                userRepository.update(it.oid(), it)
                LoginResult(true, tokens.token, tokens.refreshToken)
            } ?: LoginResult(false, null, "Sign up failed")

        } else {
            LoginResult(false, null, "Username not available")
        }
    }

}