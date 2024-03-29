package com.bikenance

import io.ktor.server.application.*
import org.koin.ktor.ext.inject

data class ApiConfig(
    val url: String,
    val rootPath: String
)

data class DBConfig(
    val host: String,
    val name: String,
    val user: String,
    val password: String
)

data class StravaConfig(
    val clientId: String,
    val clientSecret: String,
    val subscribeUrl: String,
    val subscribeOnLaunch: Boolean = true,
    val forceSubscribe : Boolean = false
)

data class FirebaseConfig(
    val serviceAccountFile: String
)

class AppConfig {
    lateinit var api: ApiConfig
    lateinit var db: DBConfig
    lateinit var strava: StravaConfig
    lateinit var firebase: FirebaseConfig
}

fun Application.loadConfig() {

    val config: AppConfig by inject()

    config.api = ApiConfig(
        environment.config.property("api.url").getString(),
        environment.config.property("ktor.deployment.rootPath").getString()
    )

    config.db = DBConfig(
        environment.config.property("db.host").getString(),
        environment.config.property("db.name").getString(),
        environment.config.property("db.user").getString(),
        environment.config.property("db.password").getString()
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
}