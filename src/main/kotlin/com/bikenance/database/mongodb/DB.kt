package com.bikenance.database.mongodb

import com.bikenance.database.BikeDao
import com.bikenance.database.BikeRideDao
import com.bikenance.database.ProfileDao
import com.bikenance.database.UserDao
import com.bikenance.features.strava.model.StravaActivity
import com.bikenance.features.strava.model.StravaAthlete
import com.bikenance.model.Bike
import com.bikenance.model.BikeRide
import com.bikenance.model.Profile
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
    val profiles = mongoDatabase.getCollection<Profile>()
    val bikes = mongoDatabase.getCollection<Bike>()
    val bikeRides = mongoDatabase.getCollection<BikeRide>()
}

class DAOS(db: DB) {
    val userDao : UserDao = MongoUserDao(db)
    val profileDao : ProfileDao = MongoProfileDao(db)
    val bikeDao : BikeDao = MongoBikeDao(db)
    val bikeRideDao: BikeRideDao = MongoBikeRideDao(db)
}


fun Application.configureMongoDB() {

    val db: DB by inject()

    if (db.users.countDocuments() == 0L) {
        db.users.insertOne(
            User(
                username = "angel", password = "angel_secret"
            )
        )
    }

}