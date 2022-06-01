package com.bikenance.features.login.routing

import com.bikenance.features.login.config.JwtMgr
import com.bikenance.repository.UserRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import org.koin.ktor.ext.inject

fun Application.loginAuthentication() {

    val jwtMgr: JwtMgr by inject()
    val userRepository: UserRepository by inject()

    authentication {
        jwt {
            verifier(jwtMgr.verifier.verifier)
            realm = jwtMgr.config.realm
            validate { credential ->
                val userId = credential.payload.subject
                val user = userRepository.getById(userId)
                if (user != null) {
                    JWTPrincipal(credential.payload)
                } else
                    null
            }
            challenge { _, _ ->
                call.respond(HttpStatusCode.Unauthorized, "Token is not valid or has expired")
            }
        }
    }
}