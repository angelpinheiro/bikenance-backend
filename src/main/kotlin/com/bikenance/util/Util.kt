package com.bikenance.util

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.util.logging.*
import kotlinx.coroutines.*
import org.koin.ktor.ext.inject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


// Log Utils

fun bknLogger(name: String) = KtorSimpleLogger(name)


// DateTime Utils

val dtf: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
fun LocalDateTime.formatAsIsoDate(): String {
    return dtf.format(this)
}


// Routing utils

fun Application.listAllRoutes() {

    val client: HttpClient by inject()

    val registeredRoutes = mutableListOf<Route>()
    routing {
        registeredRoutes.addAll(children.flatMap { it.getAllRoutes() })
    }

    environment.monitor.subscribe(ApplicationStarted) {
        val scope = CoroutineScope(Job() + Dispatchers.IO)
        scope.launch {
            delay(2000)
            client.get("http://localhost:8080/api/ping")
            log.info("API Routes: ")
            registeredRoutes.forEach {
                log.info("$it")
            }
        }
    }


}

