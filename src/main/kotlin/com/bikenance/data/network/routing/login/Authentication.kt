package com.bikenance.data.network.routing.login

import com.bikenance.data.network.jwt.JwtMgr
import com.bikenance.data.repository.UserRepository
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