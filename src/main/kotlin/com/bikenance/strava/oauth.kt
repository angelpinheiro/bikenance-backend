package com.bikenance.strava

import com.bikenance.AppConfig
import com.bikenance.database.mongodb.DB
import com.bikenance.repository.UserRepository
import com.bikenance.routing.apiResult
import com.bikenance.strava.model.StravaAthlete
import com.bikenance.strava.usecase.StravaOAuthCallbackHandler
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.logging.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.pipeline.*
import org.koin.ktor.ext.inject
import java.time.Instant

object StravaOAuthEndpoints {
    const val authorizeUrl = "https://www.strava.com/oauth/mobile/authorize"
    const val accessTokenUrl = "https://www.strava.com/api/v3/oauth/token"
}

fun Application.configureOAuth() {

    val config: AppConfig by inject()
    val strava: com.bikenance.strava.api.Strava by inject()
    val oAuthCallbackHandler: StravaOAuthCallbackHandler by inject()
    val db: DB by inject()
    val userRepository: UserRepository by inject()

    authentication {
        oauth("auth-oauth-strava") {
            urlProvider = { "${config.api.url}/callback" }
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
            client = HttpClient(CIO) {
                install(Logging)
            }
        }
    }


    routing {

        // TEST STRAVA OAUTH REFRESH TOKENS
        get("/athlete/{token}") {
            apiResult{
                call.parameters["token"]?.let {
                    userRepository.getByToken(it)?.stravaAuthData?.let { auth ->
                        // auth.expiresAt = 0 // Force token refresh
                        strava.withAuth(auth).athlete();
                    }
                }
            }
        }

        /**
         * Route for testing purposes. Authorizes a user using the strava OAuth API,
         * and stores the token in the user with id '1'
         */
        authenticate("auth-oauth-strava") {

            get("/strava") {
                // Redirects to strava authorize endpoint automatically
            }

            get("/callback") {
                val authData = getOAuthData()
                val isApp = call.request.headers["user-agent"]?.let { isMobileUserAgentRegex(it) } ?: false
                var appToken: String? = null

                if (authData != null) {
                    call.parameters["scope"]?.let { authData.scope = it }
                    appToken = oAuthCallbackHandler.handleCallback(authData)
                }

                if (isApp)
                    call.respondRedirect("bikenance://redirect?code=$appToken")
                else
                    call.respond(appToken ?: "Auth failed")

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
        return@let com.bikenance.strava.mapper.readValue<StravaAthlete>(it)
    }
}

fun isMobileUserAgentRegex(userAgent: String) =
    listOf("Mobile", "Android", "iPhone", "iPad", "IEMobile").any { userAgent.contains(it) }