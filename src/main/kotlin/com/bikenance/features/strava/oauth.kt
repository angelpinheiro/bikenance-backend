package com.bikenance.features.strava

import com.bikenance.database.mongodb.DB
import com.bikenance.features.login.config.AppConfig
import com.bikenance.features.strava.api.Strava
import com.bikenance.features.strava.model.StravaAthlete
import com.bikenance.features.strava.usecase.handleOAuthCallback
import com.bikenance.repository.UserRepository
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
    val strava: Strava by inject()
    val db: DB by inject()
    val userRepository : UserRepository by inject()

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
            val r = call.parameters["token"]?.let {
                userRepository.getByToken(it)?.authData?.let { auth->
                    // auth.expiresAt = 0 // Force token refresh
                    val stravaClient = strava.withAuth(auth);
                    call.respond(stravaClient.athlete())
                    true
                }
            }
            if(r == null){
                call.respond("WTF")
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
                val token = oAuthData()
                val isApp = call.request.headers["user-agent"]?.let { isMobileUserAgentRegex(it) } ?: false

                if (token != null) {
                    call.parameters["scope"]?.let { token.scope = it }
                    handleOAuthCallback(strava, db, token)
                }

                if (isApp)
                    call.respondRedirect("bikenance://redirect?code=$token")
                else
                    call.respond(token ?: "Auth failed")

            }
        }
    }
}

data class AuthData(
    var accessToken: String,
    var refreshToken: String?,
    var expiresIn: Long,
    var expiresAt: Long,
    var scope: String = ""
)

fun PipelineContext<*, ApplicationCall>.oAuthData(): AuthData? {
    val principal: OAuthAccessTokenResponse.OAuth2? = call.principal()
    return principal?.let {
        val expiration = Instant.now().plusSeconds(it.expiresIn - 10).toEpochMilli()
        AuthData(it.accessToken, it.refreshToken, it.expiresIn, expiration)
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