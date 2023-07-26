package com.bikenance.di

import com.bikenance.AppConfig
import com.bikenance.data.network.jwt.JwtConfig
import com.bikenance.data.network.jwt.JwtMgr
import com.bikenance.data.network.push.MessageSender
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.logging.*
import org.koin.dsl.module

val appModule = module {

    single<AppConfig> {
        AppConfig()
    }

    single<JwtMgr> {
        JwtMgr(JwtConfig())
    }

    single<HttpClient> {
        HttpClient(CIO) {
            engine {
                requestTimeout = 0
            }

            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.INFO

            }
        }
    }

    single<MessageSender> {
        MessageSender()
    }

    single<ObjectMapper> {
        jacksonObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT)
            .enable(SerializationFeature.WRITE_SELF_REFERENCES_AS_NULL)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    }

}