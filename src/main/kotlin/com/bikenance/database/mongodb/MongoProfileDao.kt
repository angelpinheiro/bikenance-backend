package com.bikenance.database.mongodb

import com.bikenance.database.BikeDao
import com.bikenance.database.BikeRideDao
import com.bikenance.database.ProfileDao
import com.bikenance.model.Bike
import com.bikenance.model.BikeRide
import com.bikenance.model.Profile
import org.litote.kmongo.eq
import org.litote.kmongo.findOne

class MongoProfileDao(db: DB) : BasicDao<Profile>(db.profiles), ProfileDao {

    override suspend fun getByUserId(id: String): Profile? {
        return collection.findOne(Profile::userId eq id)
    }

}

class MongoBikeDao(db: DB) : BasicDao<Bike>(db.bikes), BikeDao {
    override suspend fun getByStravaId(id: String): Bike? {
        return collection.findOne(Bike::stravaId eq id)
    }

    override suspend fun getByUserId(id: String): List<Bike> {
        return collection.find(Bike::userId eq id).toList()
    }

}


class MongoBikeRideDao(db: DB) : BasicDao<BikeRide>(db.bikeRides), BikeRideDao {

    override suspend fun getByStravaId(id: String): BikeRide? {
        return collection.findOne(BikeRide::stravaId eq id)
    }

    override suspend fun getByBikeId(id: String): BikeRide? {
        return collection.findOne(BikeRide::bikeId eq id)
    }

    override suspend fun getByUserId(id: String): List<BikeRide> {
        return collection.find(BikeRide::userId eq id).toList()
    }

}