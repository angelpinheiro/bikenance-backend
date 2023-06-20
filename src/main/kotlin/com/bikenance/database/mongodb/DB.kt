package com.bikenance.database.mongodb

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.LoggerContext
import com.bikenance.database.*
import com.bikenance.strava.model.StravaActivity
import com.bikenance.strava.model.StravaAthlete
import com.bikenance.login.config.AppConfig
import com.bikenance.model.Bike
import com.bikenance.model.BikeRide
import com.bikenance.model.Profile
import com.bikenance.model.User
import com.mongodb.client.MongoDatabase
import io.ktor.server.application.*
import org.koin.ktor.ext.inject
import org.litote.kmongo.KMongo
import org.litote.kmongo.getCollection
import org.slf4j.LoggerFactory

fun createDatabase(config: AppConfig): MongoDatabase {

    val loggerContext = LoggerFactory.getILoggerFactory() as LoggerContext
    val rootLogger = loggerContext.getLogger("org.mongodb.driver")
    rootLogger.level = Level.OFF

    val client = KMongo.createClient("mongodb://" + config.db.host)
    return client.getDatabase(config.db.name)
}


class DB(mongoDatabase: MongoDatabase) {
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

    val stravaAthleteDao: StravaAthleteDao = MongoStravaAthleteDao(db)
    val stravaActivityDao: StravaActivityDao = MongoStravaActivityDao(db)
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