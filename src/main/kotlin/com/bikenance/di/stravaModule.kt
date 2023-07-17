package com.bikenance.di

import com.bikenance.data.network.strava.Strava
import com.bikenance.data.network.strava.StravaWebhook
import com.bikenance.usecase.SyncStravaDataUseCase
import com.bikenance.usecase.strava.StravaBikeSync
import com.bikenance.usecase.strava.StravaEventReceivedUseCase
import com.bikenance.usecase.strava.StravaAuthCallbackHandler
import org.koin.dsl.module

val stravaModule = module {

    single<Strava> {
        Strava(get(), get(), get())
    }

    single<StravaWebhook> { StravaWebhook(get(), get(), get()) }

    single<StravaAuthCallbackHandler> { StravaAuthCallbackHandler(get(), get(), get(), get(), get(), get()) }

    single { StravaEventReceivedUseCase(get(), get(), get(), get()) }

    single { SyncStravaDataUseCase(get(), get(), get(), get(), get()) }

    single {
        StravaBikeSync(get(), get(), get(), get())
    }
}