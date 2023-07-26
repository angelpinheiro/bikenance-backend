package com.bikenance.api.strava

import com.bikenance.data.model.login.TokenPair
import com.bikenance.data.network.strava.stravaAuthConfigName
import com.bikenance.usecase.strava.StravaAuthCallbackHandler
import com.bikenance.util.bknLogger
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

data class AuthData(
    var accessToken: String,
    var refreshToken: String?,
    var expiresIn: Long,
    var expiresAt: Long,
    var scope: String = "",
    var lastRefresh: String,
)

fun Route.stravaLoginRoutes() {

    val log = bknLogger("StravaAuth")
    val oAuthCallbackHandler: StravaAuthCallbackHandler by inject()

    authenticate(stravaAuthConfigName) {

        get("/strava") {
            // Redirects to strava authorize endpoint automatically
        }

        get("/callback") {
            val authData = getOAuthData(this)
            val tokenPair: TokenPair = oAuthCallbackHandler.handleCallback(authData)
            call.parameters["scope"]?.let { authData.scope = it }
            call.respondRedirect("bikenance://redirect?code=${tokenPair.token}&refresh=${tokenPair.refreshToken}")
        }
    }
}

fun getOAuthData(pipelineContext: PipelineContext<*, ApplicationCall>): AuthData {
    val principal: OAuthAccessTokenResponse.OAuth2? = pipelineContext.call.principal()
    return if (principal != null) {
        createAuth(
            principal.accessToken, principal.refreshToken, principal.expiresIn
        )
    } else {
        throw Exception("Could not get auth principal")
    }

}

fun createAuth(
    accessToken: String,
    refreshToken: String?,
    expiresIn: Long
) : AuthData {
    val expiration = Instant.now().plusSeconds(expiresIn).epochSecond
   return AuthData(
        accessToken, refreshToken, expiresIn, expiration, lastRefresh = "Pending"
    )
}