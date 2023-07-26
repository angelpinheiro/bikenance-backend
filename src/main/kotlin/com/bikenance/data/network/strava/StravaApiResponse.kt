package com.bikenance.data.network.strava

import com.bikenance.util.bknLogger
import io.ktor.http.*

data class StravaApiError(val code: String, val field: String, val resource: String)

data class StravaApiFault(
    val errors: List<StravaApiError>, val message: String
)

val log = bknLogger("StravaApiResponse")

sealed class StravaApiResponse<T> {
    data class Success<T>(val data: T) : StravaApiResponse<T>()
    data class Fault<T>(val status: HttpStatusCode, val fault: StravaApiFault, val exception: Exception? = null) :
        StravaApiResponse<T>()

    private fun logFault(fault: Fault<T>) {
        log.error("StravaApiFault: ${fault.status} ,${fault.fault.message}, ${fault.fault.message}")
    }

    private fun faultDescription(fault: Fault<T>): String {
        return "StravaApiFault: ${fault.status} ,${fault.fault.message}, ${fault.fault.errors.joinToString { "[${it.resource} ${it.code} ${it.field}]" }}"
    }

    fun successOrElse(
        default: T
    ): T {
        return when (this) {
            is Success -> this.data
            else -> default
        }
    }

    fun successOrFail(message: String = ""): T {
        return when (this) {
            is Success -> this.data
            is Fault -> {
                throw Exception("[$message] ${faultDescription(this)}")
            }
        }
    }

    suspend fun onSuccess(
        block: (T) -> Unit
    ) {
        return when (this) {
            is Fault -> this.logFault(this)
            is Success -> block(this.data)
        }
    }
}
