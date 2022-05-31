//package com.bikenance.features.strava.routing
//
//import com.bikenance.database.mongodb.DB
//import com.bikenance.features.login.config.AppConfig
//import com.bikenance.features.strava.api.Strava
//import com.bikenance.features.strava.model.StravaRequestParams
//import com.bikenance.repository.UserRepository
//import com.fasterxml.jackson.annotation.JsonProperty
//import com.fasterxml.jackson.databind.ObjectMapper
//import com.fasterxml.jackson.module.kotlin.readValue
//import io.ktor.client.*
//import io.ktor.client.request.*
//import io.ktor.client.statement.*
//import io.ktor.server.application.*
//import io.ktor.server.response.*
//import io.ktor.server.routing.*
//import org.koin.ktor.ext.inject
//
//fun Application.stravaLogin() {
//
//    val db: DB by inject()
//    val userRepository: UserRepository by inject()
//    val strava: Strava by inject()
//    val client: HttpClient by inject()
//    val mapper: ObjectMapper by inject()
//    val config: AppConfig by inject()
//
//
//    routing {
//
//        /**
//         *
//         */
//        post("/strava/token") {
//            val code = call.parameters["code"]
//            val result = client.post("https://www.strava.com/oauth/token") {
//                parameter(StravaRequestParams.CLIENT_ID, config.strava.clientId)
//                parameter(StravaRequestParams.CLIENT_SECRET, config.strava.clientSecret)
//            }
//
//            val tokenResponse: TokenResponse = mapper.readValue(result.bodyAsText())
//            call.respond(tokenResponse.accessToken ?: "Fail")
//        }
//    }
//}
//
//data class TokenResponse(
//    @JsonProperty("token_type") var tokenType: String? = null,
//    @JsonProperty("expires_at") var expiresAt: Int? = null,
//    @JsonProperty("expires_in") var expiresIn: Int? = null,
//    @JsonProperty("refresh_token") var refreshToken: String? = null,
//    @JsonProperty("access_token") var accessToken: String? = null,
//)