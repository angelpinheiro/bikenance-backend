package com.bikenance.usecase.strava

import com.bikenance.AppConfig
import com.bikenance.StravaConfig
import com.bikenance.api.strava.AuthData
import com.bikenance.api.strava.StravaOAuthEndpoints
import com.bikenance.data.model.User
import com.bikenance.data.model.strava.StravaRequestParams
import com.bikenance.data.repository.UserRepository
import com.bikenance.util.formatAsIsoDate
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.server.util.*
import io.ktor.util.*
import java.time.Instant
import java.time.LocalDateTime
import java.util.*

data class RefreshTokenResponse(
    @JsonProperty("token_type") var tokenType: String,
    @JsonProperty("expires_at") var expiresAt: Long,
    @JsonProperty("expires_in") var expiresIn: Long,
    @JsonProperty("refresh_token") var refreshToken: String,
    @JsonProperty("access_token") var accessToken: String,
)

interface TokenRefresher {
    suspend fun refreshAccessTokenIfNecessary(auth: AuthData): AuthData
}

class StravaTokenRefresh(
    private val config: AppConfig,
    private val client: HttpClient,
    private val mapper: ObjectMapper,
    private val userRepository: UserRepository
) : TokenRefresher {

    override suspend fun refreshAccessTokenIfNecessary(auth: AuthData): AuthData {
        if (needsTokenRefresh(auth)) {
            val user = findTargetUser(auth);
            user?.let {
                val newAuth = performTokenRefresh(auth)
                updateUserAuthInDB(user, newAuth)
                return newAuth
            }
        }
        return auth
    }

    @OptIn(InternalAPI::class)
    private fun needsTokenRefresh(auth: AuthData): Boolean {
        val expirationSeconds = auth.expiresAt
        val nowSeconds = Instant.now().epochSecond
        val expiration = Date(expirationSeconds * 1000).toLocalDateTime().formatAsIsoDate()
        val now = Date(nowSeconds * 1000).toLocalDateTime().formatAsIsoDate()
        println("Token expires at $expiration ($expirationSeconds) | $now ($nowSeconds)")
        return expirationSeconds + 60 <= nowSeconds
    }

    private suspend fun updateUserAuthInDB(user: User, auth: AuthData): User? {
        user.stravaAuthData = auth
        return userRepository.update(user.oid(), user)
    }

    private suspend fun findTargetUser(auth: AuthData): User? {
        return userRepository.getByToken(auth.accessToken);
    }

    private suspend fun refreshAccessToken(token: String): RefreshTokenResponse {
        val response = client.post(StravaOAuthEndpoints.accessTokenUrl) {
            parameter(StravaRequestParams.CLIENT_ID, config.strava.clientId)
            parameter(StravaRequestParams.CLIENT_SECRET, config.strava.clientSecret)
            parameter(StravaRequestParams.GRANT_TYPE, "refresh_token")
            parameter(StravaRequestParams.REFRESH_TOKEN, token)
        }
        println("Refreshing access token (${response.status})")
        return mapper.readValue(response.bodyAsText())
    }

    private suspend fun performTokenRefresh(auth: AuthData): AuthData {
        // TODO: Error handling
        val refreshToken = auth.refreshToken ?: throw RuntimeException("Refresh token is null")
        println("Refreshing access token")
        val response = refreshAccessToken(refreshToken)
        return AuthData(
            response.accessToken,
            response.refreshToken,
            response.expiresIn,
            response.expiresAt,
            auth.scope,
            LocalDateTime.now().formatAsIsoDate()
        )
    }
}
