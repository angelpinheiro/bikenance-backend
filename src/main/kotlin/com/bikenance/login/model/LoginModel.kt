package com.bikenance.login.model

import io.ktor.server.auth.*

data class TokenPair(
    val token : String,
    val refreshToken: String
)

data class AuthData(
    val username: String,
)
data class LoginData(
    val username: String,
    val password: String,
) : Principal


data class RefreshData(
    val refreshToken: String,
)
