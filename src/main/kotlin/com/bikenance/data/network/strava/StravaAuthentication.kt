package com.bikenance.data.network.strava

import com.bikenance.AppConfig
import com.bikenance.api.strava.StravaOAuthEndpoints
import com.bikenance.data.network.jwt.JwtMgr
import com.bikenance.data.repository.UserRepository
import com.bikenance.usecase.strava.StravaOAuthCallbackHandler
import com.bikenance.util.bknLogger
import io.ktor.client.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import org.koin.ktor.ext.inject

const val stravaAuthConfigName = "strava-oauth"
fun Application.stravaAuthentication() {

    val config: AppConfig by inject()
    val httpClient: HttpClient by inject()

    authentication {
        oauth(stravaAuthConfigName) {
            urlProvider = { "${config.api.url}/${config.api.rootPath}/callback" }
            providerLookup = {
                OAuthServerSettings.OAuth2ServerSettings(
                    name = "strava",
                    authorizeUrl = StravaOAuthEndpoints.authorizeUrl,
                    accessTokenUrl = StravaOAuthEndpoints.accessTokenUrl,
                    requestMethod = HttpMethod.Post,
                    clientId = config.strava.clientId,
                    clientSecret = config.strava.clientSecret,
                    defaultScopes = listOf("read_all,activity:read_all,profile:read_all"),
                )
            }
            client = httpClient
        }
    }
}