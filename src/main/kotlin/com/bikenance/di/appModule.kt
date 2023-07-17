package com.bikenance.di

import com.bikenance.AppConfig
import com.bikenance.data.database.UserDao
import com.bikenance.data.database.mongodb.*
import com.bikenance.data.network.jwt.JwtConfig
import com.bikenance.data.network.jwt.JwtMgr
import com.bikenance.data.network.push.MessageSender
import com.bikenance.data.network.strava.Strava
import com.bikenance.data.network.strava.StravaWebhook
import com.bikenance.data.repository.UserRepository
import com.bikenance.usecase.SyncStravaDataUseCase
import com.bikenance.usecase.strava.ReceiveDataUseCase
import com.bikenance.usecase.strava.StravaBikeSync
import com.bikenance.usecase.strava.StravaOAuthCallbackHandler
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
            engine {
                requestTimeout = 0
            }

            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.INFO

            }
        }
    }

    single {
        Strava(get(), get(), get())
    }

    single { StravaWebhook(get(), get(), get()) }

    single { StravaOAuthCallbackHandler(get(), get(), get(), get(), get(), get()) }

    single { ReceiveDataUseCase(get(), get(), get(), get()) }

    single { SyncStravaDataUseCase(get(), get(), get(), get(), get()) }




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