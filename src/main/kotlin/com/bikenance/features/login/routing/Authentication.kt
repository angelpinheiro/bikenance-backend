package com.bikenance.features.login.routing

import com.bikenance.features.login.JwtConfig
import com.bikenance.features.login.JwtVerifier
import com.bikenance.features.login.data.LoginData
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

fun Application.loginAuthentication(config: JwtConfig) {

    val jwtVerifier = JwtVerifier(config)

    authentication {
        jwt {
            verifier(jwtVerifier.verifier)
            realm = config.realm
            validate {
                val name = it.payload.getClaim("username").asString()
                val password = it.payload.getClaim("password").asString()
                if(name != null && password != null){
                    LoginData(name, password)
                }else{
                    null
                }
            }
        }
    }
}