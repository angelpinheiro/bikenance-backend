package com.bikenance.modules

import com.bikenance.database.UserDaoFacade
import com.bikenance.database.mongodb.DB
import com.bikenance.database.mongodb.MongoUserDao
import com.bikenance.features.strava.api.Strava
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
        DB()
    }

    single<UserDaoFacade> {
        MongoUserDao(get())
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
        Strava(get())
    }

    single {
        jacksonObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT)
            .enable(SerializationFeature.WRITE_SELF_REFERENCES_AS_NULL)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    }

}