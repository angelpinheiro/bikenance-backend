package com.bikenance.data.database.mongodb

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.LoggerContext
import com.bikenance.AppConfig
import com.bikenance.data.database.*
import com.bikenance.data.model.Bike
import com.bikenance.data.model.BikeRide
import com.bikenance.data.model.Profile
import com.bikenance.data.model.User
import com.bikenance.data.model.serializer.BikeTypesModule
import com.bikenance.data.model.strava.StravaActivity
import com.bikenance.data.model.strava.StravaAthlete
import com.mongodb.client.MongoDatabase
import org.litote.kmongo.KMongo
import org.litote.kmongo.getCollection
import org.litote.kmongo.util.KMongoConfiguration
import org.slf4j.LoggerFactory

fun createDatabase(config: AppConfig): MongoDatabase {

    val loggerContext = LoggerFactory.getILoggerFactory() as LoggerContext
    val rootLogger = loggerContext.getLogger("org.mongodb.driver")
    rootLogger.level = Level.OFF

    val hasAuth = config.db.name.isNotBlank() && config.db.password.isNotBlank()

    val connectionString = if (hasAuth) {
        "mongodb://${config.db.user}:${config.db.password}@${config.db.host}"
    } else {
        "mongodb://${config.db.host}"
    }


    KMongoConfiguration.registerBsonModule(BikeTypesModule())

    val client = KMongo.createClient(connectionString)
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
    val userDao: UserDao = MongoUserDao(db)
    val profileDao: ProfileDao = MongoProfileDao(db)
    val bikeDao: BikeDao = MongoBikeDao(db)
    val bikeRideDao: BikeRideDao = MongoBikeRideDao(db)
//    val componentDao: MongoComponentDao = MongoComponentDao(db)

    val stravaAthleteDao: StravaAthleteDao = MongoStravaAthleteDao(db)
    val stravaActivityDao: StravaActivityDao = MongoStravaActivityDao(db)
}