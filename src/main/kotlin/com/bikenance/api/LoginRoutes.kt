package com.bikenance.api

import com.bikenance.data.network.jwt.JwtMgr
import com.bikenance.data.model.login.AuthData
import com.bikenance.data.model.login.LoginData
import com.bikenance.data.model.login.RefreshData
import com.bikenance.usecase.LoginUseCase
import com.bikenance.data.repository.UserRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

val ApplicationCall.authData get() = authentication.principal<LoginData>()?.let { AuthData(it.username)  }

fun Route.loginRoutes() {

    val userRepository: UserRepository by inject()
    val jwtMgr: JwtMgr by inject()

    val loginUseCase = LoginUseCase(jwtMgr, userRepository)

    /**
     *
     */
    post("/login") {
        val user = call.receive<LoginData>()
        val loginResult = loginUseCase.loginUser(user)
        if (loginResult.success) {
            call.respond(HttpStatusCode.OK, loginResult)
        } else {
            call.respond(HttpStatusCode.Unauthorized, loginResult.message ?: "Unauthorized")
        }
    }

    post("/register") {
        val user = call.receive<LoginData>()
        val registerResult = loginUseCase.registerUser(user)
        if (registerResult.success) {
            call.respond(HttpStatusCode.OK, registerResult)
        } else {
            call.respond(HttpStatusCode.Unauthorized, registerResult.message ?: "Registration failed")
        }
    }

    post("/refresh") {
        val refreshData = call.receive<RefreshData>()
        apiResult {
            loginUseCase.refreshUserTokens(refreshData)
        }
    }

    authenticate {
        get("/authenticate") {
            when (val u = call.authData) {
                null -> call.respond(HttpStatusCode.Unauthorized, "Unauthorized")
                else -> {
                    call.respond("Hi ${u.username}!")
                }
            }
        }

        get("/secrets") {
            call.respond("Secrets!")
        }

    }

}
