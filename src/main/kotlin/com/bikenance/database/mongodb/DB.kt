package com.bikenance.database.mongodb

import com.bikenance.features.strava.model.StravaActivity
import com.bikenance.features.strava.model.StravaAthlete
import com.bikenance.model.User
import com.mongodb.client.MongoDatabase
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