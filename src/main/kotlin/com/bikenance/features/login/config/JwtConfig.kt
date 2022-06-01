package com.bikenance.features.login.config

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.bikenance.features.login.data.LoginData
import com.bikenance.features.strava.AuthData
import com.bikenance.features.strava.model.StravaAthlete
import com.bikenance.model.User
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
     * Produce a token for user / password login
     */
    fun generateToken(user: User): String = JWT.create()
        .withSubject(user.id())
        .withIssuer(config.issuer)
        .withExpiresAt(getExpiration())  // optional
        .sign(config.algorithm)

    fun generateToken(user: User, athlete: StravaAthlete, authData: AuthData ): String = JWT.create()
        .withSubject(user.id())
        .withIssuer(config.issuer)
        .withExpiresAt(Date(authData.expiresAt*1000)) // same as strava token
        .sign(config.algorithm)

    /**
     * Calculate the expiration Date based on current time + the given validity
     */
    private fun getExpiration() = Date(System.currentTimeMillis() + config.validityInMs)

}