package com.bikenance.extensions

import com.bikenance.data.database.mongodb.DB
import com.bikenance.data.model.User
import com.bikenance.data.model.serializer.BikeTypesModule
import io.ktor.server.application.*
import org.koin.ktor.ext.inject
import org.litote.kmongo.util.KMongoConfiguration


fun Application.configureMongoDB() {

    KMongoConfiguration.registerBsonModule(BikeTypesModule())

    val db: DB by inject()

    log.info("Initializing mongo database...")

    if (db.users.countDocuments() == 0L) {
        db.users.insertOne(
            User(
                username = "angel", password = "angel_secret"
            )
        )
    }

    log.info("MongoDB ready.")

}