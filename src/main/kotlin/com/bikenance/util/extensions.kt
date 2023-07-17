package com.bikenance.util

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.util.pipeline.*

/**
 * Extension function for obtaining the userId from a JWT authenticated request
 */
fun PipelineContext<*, ApplicationCall>.authUserId(): String? {
    val principal: JWTPrincipal? = call.principal()
    return principal?.subject
}


fun PipelineContext<*, ApplicationCall>.authUserIdOrFail(): String {
    val principal: JWTPrincipal? = call.principal()
    return principal?.subject ?: throw Exception("Could not get authentication principal")
}