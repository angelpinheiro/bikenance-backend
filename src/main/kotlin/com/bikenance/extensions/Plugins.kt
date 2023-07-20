package com.bikenance.extensions

import com.bikenance.data.model.serializer.BikeTypesModule
import com.bikenance.data.model.serializer.DateTimeModule
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.defaultheaders.*
import io.ktor.server.resources.*
import org.litote.kmongo.id.jackson.IdJacksonModule

fun Application.configurePlugins() {

    install(DefaultHeaders) {
        header("X-Engine", "Ktor")
    }

    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
            enable(SerializationFeature.WRITE_SELF_REFERENCES_AS_NULL)
            disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            registerModule(IdJacksonModule())
            registerModule(DateTimeModule())
            registerModule(BikeTypesModule())
        }
    }

    install(Resources)

    install(CallLogging)

    install(Authentication)
}
