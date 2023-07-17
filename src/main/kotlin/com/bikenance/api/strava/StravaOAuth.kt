package com.bikenance.api.strava

import com.bikenance.AppConfig
import com.bikenance.data.database.mongodb.DB
import com.bikenance.data.model.login.TokenPair
import com.bikenance.data.repository.UserRepository
import com.bikenance.data.model.strava.StravaAthlete
import com.bikenance.data.network.stravaApi.Strava
import com.bikenance.util.mapper
import com.bikenance.usecase.strava.StravaOAuthCallbackHandler
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.client.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.logging.*
import io.ktor.util.pipeline.*
import org.koin.ktor.ext.inject
import java.time.Instant

object StravaOAuthEndpoints {
    const val authorizeUrl = "https://www.strava.com/oauth/mobile/authorize"
    const val accessTokenUrl = "https://www.strava.com/api/v3/oauth/token"
}

fun Application.configureOAuth() {
    val log = KtorSimpleLogger("StravaAuth")
    val config: AppConfig by inject()
    val strava: Strava by inject()
    val oAuthCallbackHandler: StravaOAuthCallbackHandler by inject()
    val db: DB by inject()
    val userRepository: UserRepository by inject()
    val httpClient: HttpClient by inject()

    authentication {
        oauth("auth-oauth-strava") {
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


    routing {

        authenticate("auth-oauth-strava") {

            get("/strava") {
                // Redirects to strava authorize endpoint automatically
            }

            get("/callback") {

                log.debug("Received auth callback")

                val authData = getOAuthData()
                if (authData != null) {
                    val tokenPair: TokenPair = oAuthCallbackHandler.handleCallback(authData)
                    call.parameters["scope"]?.let { authData.scope = it }
                    call.respondRedirect("bikenance://redirect?code=${tokenPair.token}&refresh=${tokenPair.refreshToken}")
                } else {
                    call.respond("Auth failed")
                }

            }
        }
    }
}

data class AuthData(
    var accessToken: String,
    var refreshToken: String?,
    var expiresIn: Long,
    var expiresAt: Long,
    var scope: String = "",
    var lastRefresh: String,
)

fun PipelineContext<*, ApplicationCall>.getOAuthData(): AuthData? {
    val principal: OAuthAccessTokenResponse.OAuth2? = call.principal()
    return principal?.let {
        val expiration = Instant.now().plusSeconds(it.expiresIn - 10).epochSecond
        AuthData(it.accessToken, it.refreshToken, it.expiresIn, expiration, lastRefresh = "Pending")
    }
}

fun PipelineContext<*, ApplicationCall>.getAthleteParameter(): StravaAthlete? {
    val principal: OAuthAccessTokenResponse.OAuth2? = call.principal()

    return principal?.extraParameters?.get("athlete")?.let {
        return@let mapper.readValue<StravaAthlete>(it)
    }
}

fun isMobileUserAgentRegex(userAgent: String) =
    listOf("Mobile", "Android", "iPhone", "iPad", "IEMobile").any { userAgent.contains(it) }