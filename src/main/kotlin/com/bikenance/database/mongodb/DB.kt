package com.bikenance.database.mongodb

import com.bikenance.features.strava.model.StravaActivity
import com.bikenance.features.strava.model.StravaAthlete
import com.bikenance.model.User
import com.mongodb.client.MongoDatabase
import io.ktor.server.application.*
import org.koin.ktor.ext.inject
import org.litote.kmongo.KMongo
import org.litote.kmongo.getCollection

fun createDatabase(): MongoDatabase {
    val client = KMongo.createClient()
    return client.getDatabase("stravadb")
}


class DB(mongoDatabase: MongoDatabase = createDatabase()) {
    val database = mongoDatabase
    val users = mongoDatabase.getCollection<User>()
    val activities = mongoDatabase.getCollection<StravaActivity>()
    val athletes = mongoDatabase.getCollection<StravaAthlete>()
}

fun Application.initializeMongo() {

    val db: DB by inject()

    if (db.users.countDocuments() == 0L) {
        db.users.insertOne(
            User(
                username = "angel", password = "angel_secret"
            )
        )
    }

}