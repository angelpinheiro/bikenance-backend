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

//sealed class ApiResult<T>(
//    val success: Boolean = true,
//    val message: String? = null,
//    val status: Int = HttpStatusCode.OK.value,
//) {
//    class Success<T>(val data: T) : ApiResult<T>()
//    class Error<T>(
//        message: String? = null,
//        status: HttpStatusCode = HttpStatusCode.InternalServerError,
//    ) : ApiResult<T>(false, message, status.value)
//}

suspend inline fun <reified T : Any> PipelineContext<Unit, ApplicationCall>.apiResult(block: PipelineContext<Unit, ApplicationCall>.() -> T?) {
    try {
        when (val result = block()) {
            null -> call.respond(HttpStatusCode.NotFound)
            else -> call.respond(status = HttpStatusCode.OK, result)
        }
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        e.printStackTrace() //TODO
        call.respond(HttpStatusCode.InternalServerError, e.message ?: "Internal server error")
    }
}

val dtf: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
fun LocalDateTime.formatAsIsoDate(): String {
    return dtf.format(this)
}

fun PipelineContext<*, ApplicationCall>.authUserId(): String? {
    val principal: JWTPrincipal? = call.principal()
    return principal?.subject
}
