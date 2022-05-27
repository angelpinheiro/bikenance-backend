package com.bikenance.modules

import com.bikenance.database.DatabaseFactory
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.defaultheaders.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import kotlinx.coroutines.launch
import org.slf4j.event.Level

fun Application.configureServer() {

    DatabaseFactory.init()

    launch {
        DatabaseFactory.populateDatabase()
    }

    install(DefaultHeaders) {
        header("X-Engine", "Ktor")
    }

    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
            enable(SerializationFeature.WRITE_SELF_REFERENCES_AS_NULL)
            disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        }
    }

    install(Resources)

    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
    }

    install(Authentication)
}
