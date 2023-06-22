package com.bikenance.routing

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.util.pipeline.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.CancellationException


/**
 * Handles the API results and generates the appropriate HTTP responses in the context of a Ktor route.
 * It executes the provided code block and handles the response based on the result.
 * - If the result is null, it responds with a 404 (Not Found) status code.
 * - If the result is not null, it responds with a 200 (OK) status code and sends the result as the response body.
 */
suspend inline fun <reified T : Any> PipelineContext<Unit, ApplicationCall>.apiResult(block: PipelineContext<Unit, ApplicationCall>.() -> T?) {
    try {
        when (val result = block()) {
            null -> call.respond(HttpStatusCode.NotFound)
            else -> call.respond(status = HttpStatusCode.OK, result)
        }
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        application.environment.log.error("An error occurred while building an api response", e)
        call.respond(HttpStatusCode.InternalServerError, e.message ?: "Internal server error")
    }
}

val dtf: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
fun LocalDateTime.formatAsIsoDate(): String {
    return dtf.format(this)
}

/**
 * Extension function for obtaining the userId from a JWT authenticated request
 */
fun PipelineContext<*, ApplicationCall>.authUserId(): String? {
    val principal: JWTPrincipal? = call.principal()
    return principal?.subject
}
