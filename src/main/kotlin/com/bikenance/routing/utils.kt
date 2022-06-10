package com.bikenance.routing

import com.bikenance.model.User
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.pipeline.*
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
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
        when (val r = block()) {
            null -> ApiResult.Error("Not found", status = HttpStatusCode.NotFound)
            else -> ApiResult.Success(r)
        }
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        e.printStackTrace()
        ApiResult.Error(e.message)
    }
    call.respond(status = HttpStatusCode.fromValue(r.status), r)
}


suspend inline fun <reified T : Any> PipelineContext<Unit, ApplicationCall>.apiResponse(block: PipelineContext<Unit, ApplicationCall>.() -> T?) {
    val r = try {
        when (val b = block()) {
            null -> call.respond("Fail")
            else -> call.respond(b)
        }
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        call.respond("Fail")
    }

}

val dtf: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
fun LocalDateTime.formatAsIsoDate(): String? {
    return dtf.format(this)
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
