package com.bikenance.api

import com.bikenance.util.authUserIdOrFail
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.util.pipeline.*
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

/**
 * Handles the API results and generates the appropriate HTTP responses in the context of a Ktor route, passing
 * authorization data to the code block to be executed
 */
suspend inline fun <reified T : Any> PipelineContext<Unit, ApplicationCall>.authApiResult(block: PipelineContext<Unit, ApplicationCall>.(userId: String) -> T?) {
    val userId = authUserIdOrFail()
    apiResult(block = { block(userId) })
}

