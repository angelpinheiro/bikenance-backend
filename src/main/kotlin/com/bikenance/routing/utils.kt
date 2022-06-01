package com.bikenance.routing

import com.bikenance.model.User
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.pipeline.*
import java.util.concurrent.CancellationException

sealed class ApiResult<T>(
    val success: Boolean = true,
    val message: String? = null,
    val status: Int = HttpStatusCode.OK.value,
    val data: T? = null,
) {
    class Success<T>(data: T) : ApiResult<T>(data = data)
    class Error<T>(
        message: String? = null,
        status: HttpStatusCode = HttpStatusCode.InternalServerError,
        data: T? = null
    ) : ApiResult<T>(false, message, status.value, data)
}

suspend inline fun <T> PipelineContext<Unit, ApplicationCall>.apiResult(block: PipelineContext<Unit, ApplicationCall>.() -> T?) {
    val r = try {
        when (block()) {
            null -> ApiResult.Error("Not found", status = HttpStatusCode.NotFound)
            else -> ApiResult.Success(block())
        }
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        ApiResult.Error(e.message)
    }
    call.respond(status = HttpStatusCode.fromValue(r.status), r)
}


fun PipelineContext<*, ApplicationCall>.authUserId(): String? {
    val principal: JWTPrincipal? = call.principal()
    return principal?.subject
}

fun Application.test() {
    routing {
        get("/test1") {
            apiResult { null }
        }
        get("/test2") {
            apiResult {
                throw Exception("Exception!!!!!")
            }
        }
        get("/test3") {
            apiResult {
                User("hola", "que tal")
            }
        }
    }
}
