package com.bikenance

import io.ktor.server.application.*
import org.koin.ktor.ext.inject

data class ApiConfig(
    val url: String
)

data class DBConfig(
    val host: String,
    val name: String
)

data class StravaConfig(
    val clientId: String,
    val clientSecret: String,
    val subscribeUrl: String,
    val subscribeOnLaunch: Boolean = true,
    val forceSubscribe : Boolean = false
)

data class StorageConfig(
    val imageUploadFolder: String
)

data class FirebaseConfig(
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
        environment.config.property("db.host").getString(),
        environment.config.property("db.name").getString()
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
        environment.config.property("firebase.service_account_file").getString()
    )




    log.info("==== App Config ====")
    log.info("==== API ====")
    log.info(config.api.toString())
    log.info("==== DB ====")
    log.info(config.db.toString())
    log.info("==== STORAGE ====")
    log.info(config.storage.toString())
    log.info("==== FIREBASE ====")
    log.info(config.firebase.toString())
}