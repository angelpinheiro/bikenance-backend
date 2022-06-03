package com.bikenance.database.mongodb

import com.bikenance.database.BikeRideDao
import com.bikenance.model.BikeRide
import org.litote.kmongo.eq
import org.litote.kmongo.findOne

class MongoBikeRideDao(db: DB) : BasicDaoImpl<BikeRide>(db.bikeRides), BikeRideDao {

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