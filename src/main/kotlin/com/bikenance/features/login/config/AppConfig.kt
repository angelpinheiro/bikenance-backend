package com.bikenance.features.login.config

import io.ktor.server.application.*
import org.koin.ktor.ext.inject

class ApiConfig(
    val url: String
)

class DBConfig(
    val host: String
)

class StravaConfig(
    val clientId: String,
    val clientSecret: String,
    val subscribeUrl: String,
    val subscribeOnLaunch: Boolean = true,
    val forceSubscribe : Boolean = false
)

class StorageConfig(
    val imageUploadFolder: String
)

class FirebaseConfig(
    val serverKey: String,
    val serviceAccountFile: String
)

class AppConfig {
    lateinit var api: ApiConfig
    lateinit var db: DBConfig
    lateinit var strava: StravaConfig
    lateinit var storage: StorageConfig
    lateinit var firebase: FirebaseConfig
}

fun Application.loadConfig() {

    val config: AppConfig by inject()

    config.api = ApiConfig(
        environment.config.property("api.url").getString()
    )

    config.db = DBConfig(
        environment.config.property("db.host").getString()
    )

    config.storage = StorageConfig(
        environment.config.property("storage.image_upload_folder").getString()
    )

    config.strava = StravaConfig(
        environment.config.property("strava.client_id").getString(),
        environment.config.property("strava.client_secret").getString(),
        environment.config.property("strava.subscribe_url").getString(),
        environment.config.property("strava.subscribe_on_launch").getString().toBoolean(),
        environment.config.property("strava.force_subscribe").getString().toBoolean()
    )

    config.firebase = FirebaseConfig(
        environment.config.property("firebase.server_key").getString(),
        environment.config.property("firebase.service_account_file").getString()
    )
}