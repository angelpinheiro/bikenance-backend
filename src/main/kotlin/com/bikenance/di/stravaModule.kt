package com.bikenance.di

import com.bikenance.AppConfig
import com.bikenance.data.network.strava.StravaApi
import com.bikenance.data.network.strava.StravaWebhook
import com.bikenance.usecase.SyncStravaDataUseCase
import com.bikenance.usecase.strava.*
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.koin.dsl.module

val stravaModule = module {

    single<ObjectMapper> {
        jacksonObjectMapper()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    }

    single<TokenRefresher> {
        StravaTokenRefresh(get(), get(), get(), get())
    }

    single<StravaApi> {
        StravaApi(get(), get(), get())
    }

    single<StravaWebhook> { StravaWebhook(get(), get(), get()) }

    single<StravaAuthCallbackHandler> { StravaAuthCallbackHandler(get(), get(), get(), get(), get(), get()) }

    single { StravaEventReceivedUseCase(get(), get(), get(), get()) }

    single { SyncStravaDataUseCase(get(), get(), get(), get()) }

//    single {
//        StravaBikeSync(get(), get(), get(), get())
//    }
}