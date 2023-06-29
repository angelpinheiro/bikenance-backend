package com.bikenance.login.config

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.bikenance.login.model.TokenPair
import com.bikenance.model.User
import com.bikenance.strava.AuthData
import com.bikenance.strava.model.StravaAthlete
import java.util.*


class JwtMgr(
    val config: JwtConfig,
    val generator: JwtGenerator = JwtGenerator(config),
    val verifier: JwtVerifier = JwtVerifier(config)
)

data class JwtConfig(
    val issuer: String = "com.bikenance",
    val realm: String = "com.bikenance",
    val secret: String = "com.bikenance.secret",
    val validityInMs: Long = 36_000_00 * 24, // 1 day
    val refreshValidityInMs: Long = 36_000_00 * 24 * 14, // 2 weeks
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
     * Produce a token for user / password login
     */
    private fun generateToken(user: User): String = JWT.create()
        .withSubject(user.oid())
        .withIssuer(config.issuer)
        .withExpiresAt(getExpiration())  // optional
        .sign(config.algorithm)


    private fun generateRefreshToken(user: User): String = JWT.create()
        .withSubject(user.oid())
        .withIssuer(config.issuer)
        .withExpiresAt(getRefreshExpiration())
        .sign(config.algorithm)

    fun generateTokenPair(user: User) =
        TokenPair(
            generateToken(user),
            generateRefreshToken(user)
        )

    /**
     * Calculate the expiration Date based on current time + the given validity
     */
    private fun getExpiration() = Date(System.currentTimeMillis() + config.validityInMs)

    private fun getRefreshExpiration() = Date(System.currentTimeMillis() + config.refreshValidityInMs)

}