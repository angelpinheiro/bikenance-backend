package com.bikenance.modules

import com.bikenance.AppConfig
import com.bikenance.database.UserDao
import com.bikenance.database.mongodb.*
import com.bikenance.login.config.JwtConfig
import com.bikenance.login.config.JwtMgr
import com.bikenance.push.MessageSender
import com.bikenance.repository.UserRepository
import com.bikenance.strava.usecase.ReceiveDataUseCase
import com.bikenance.strava.usecase.StravaBikeSync
import com.bikenance.strava.usecase.StravaOAuthCallbackHandler
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.logging.*
import io.ktor.util.logging.*
import org.koin.dsl.module



val appModule = module {

    single {
        AppConfig()
    }

    single {
        DB(createDatabase(get()))
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
        com.bikenance.strava.api.Strava(get(), get(), get())
    }

    single { StravaOAuthCallbackHandler(get(), get(), get(), get()) }

    single { ReceiveDataUseCase(get(), get(), get(), get()) }

    single {
        MessageSender(get(), get(), get())
    }

    single {
        StravaBikeSync(get(), get(), get(), get())
    }

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