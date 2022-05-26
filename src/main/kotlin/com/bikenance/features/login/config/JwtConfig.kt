package com.bikenance.features.login

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.bikenance.features.login.data.LoginData
import java.util.*


data class JwtConfig(
    val issuer: String = "com.bikenance",
    val realm: String = "com.bikenance",
    val secret: String = "com.bikenance.secret",
    val validityInMs: Long = 36_000_00 * 24,
    val algorithm: Algorithm = Algorithm.HMAC512(secret)
)

class JwtVerifier(private val config: JwtConfig) {
    val verifier: JWTVerifier = JWT
        .require(config.algorithm)
        .withIssuer(config.issuer)
        .build()
}

class JwtGenerator(private val config: JwtConfig) {

    /**
     * Produce a token for this combination of name and password
     */
    fun generateToken(user: LoginData): String = JWT.create()
        .withSubject("Authentication")
        .withIssuer(config.issuer)
        .withClaim("username", user.username)
        .withClaim("password", user.password)
        .withExpiresAt(getExpiration())  // optional
        .sign(config.algorithm)

    /**
     * Calculate the expiration Date based on current time + the given validity
     */
    private fun getExpiration() = Date(System.currentTimeMillis() + config.validityInMs)
}