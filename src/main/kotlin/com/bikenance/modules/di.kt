package com.bikenance.modules

import com.bikenance.database.UserDao
import com.bikenance.database.mongodb.*
import com.bikenance.features.login.config.AppConfig
import com.bikenance.features.login.config.JwtConfig
import com.bikenance.features.login.config.JwtMgr
import com.bikenance.features.strava.api.Strava
import com.bikenance.features.strava.usecase.StravaOAuthCallbackHandler
import com.bikenance.model.Profile
import com.bikenance.repository.UserRepository
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.logging.*
import org.koin.dsl.module


val appModule = module {

    single {
        AppConfig()
    }

    single {
        DB()
    }

    single { DAOS(get()) }

    single<UserDao> {
        MongoUserDao(get())
    }
    single {
        MongoProfileDao(get())
    }

    single {
        MongoBikeDao(get())
    }

    single {
        MongoBikeRideDao(get())
    }

    single {
        UserRepository(get())
    }

    single {
        HttpClient(CIO) {
            install(Logging)
        }
    }

    single {
        Strava(get(), get(), get())
    }

    single { StravaOAuthCallbackHandler(get(), get(), get(), get()) }

    single {
        JwtMgr(JwtConfig())
    }

    single {
        jacksonObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT)
            .enable(SerializationFeature.WRITE_SELF_REFERENCES_AS_NULL)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    }

}