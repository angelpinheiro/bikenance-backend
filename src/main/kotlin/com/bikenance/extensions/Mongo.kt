package com.bikenance.extensions

import com.bikenance.data.database.mongodb.DB
import com.bikenance.data.model.User
import io.ktor.server.application.*
import org.koin.ktor.ext.inject


fun Application.configureMongoDB() {

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