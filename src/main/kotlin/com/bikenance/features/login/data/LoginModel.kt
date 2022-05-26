package com.bikenance.features.login.data

import io.ktor.server.auth.*


data class AuthData(
    val username: String,
)

data class LoginData(
    val username: String,
    val password: String,
) : Principal
