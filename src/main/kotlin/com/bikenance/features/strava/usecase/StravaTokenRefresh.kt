package com.bikenance.features.strava.usecase

import com.bikenance.features.login.config.StravaConfig
import com.bikenance.features.strava.AuthData
import com.bikenance.features.strava.StravaOAuthEndpoints
import com.bikenance.features.strava.api.mapper
import com.bikenance.features.strava.model.StravaRequestParams
import com.bikenance.model.User
import com.bikenance.repository.UserRepository
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import java.time.Instant

data class RefreshTokenResponse(
    @JsonProperty("token_type") var tokenType: String,
    @JsonProperty("expires_at") var expiresAt: Long,
    @JsonProperty("expires_in") var expiresIn: Long,
    @JsonProperty("refresh_token") var refreshToken: String,
    @JsonProperty("access_token") var accessToken: String,
)

class StravaTokenRefresh(
    private val client: HttpClient,
    val config: StravaConfig,
    private val userRepository: UserRepository
) {

    suspend fun refreshAccessTokenIfNecessary(auth: AuthData): AuthData {
        if (needsTokenRefresh(auth)) {
            val newAuth = performTokenRefresh(auth)
            findTargetUser(auth)?.let {
                updateUserAuthInDB(it, auth)
            }
            return newAuth
        }
        return auth
    }

    private fun needsTokenRefresh(auth: AuthData): Boolean {
        return auth.expiresAt <= Instant.now().epochSecond
    }

    private suspend fun updateUserAuthInDB(user: User, auth: AuthData) {
        user.authData = auth
        userRepository.update(user.oid(), user)
    }

    private suspend fun findTargetUser(auth: AuthData): User? {
        return userRepository.getByToken(auth.accessToken);
    }

    private suspend fun refreshAccessToken(token: String): RefreshTokenResponse {
        val response = client.post(StravaOAuthEndpoints.accessTokenUrl) {
            parameter(StravaRequestParams.CLIENT_ID, config.clientId)
            parameter(StravaRequestParams.CLIENT_SECRET, config.clientSecret)
            parameter(StravaRequestParams.GRANT_TYPE, "refresh_token")
            parameter(StravaRequestParams.REFRESH_TOKEN, token)
        }
        return mapper.readValue(response.bodyAsText())
    }

    private suspend fun performTokenRefresh(auth: AuthData): AuthData {
        // TODO: Error handling
        val refreshToken = auth.refreshToken ?: throw RuntimeException("Refresh token is null")
        val response = refreshAccessToken(refreshToken)
        return AuthData(
            response.accessToken,
            response.refreshToken,
            response.expiresIn,
            response.expiresAt,
            auth.scope
        )
    }
}
