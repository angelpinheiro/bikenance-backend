package com.bikenance.database.mongodb

import com.bikenance.database.BikeRideDao
import com.bikenance.model.BikeRide
import org.bson.types.ObjectId
import org.litote.kmongo.*
import org.litote.kmongo.id.toId

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

    override suspend fun getByUserIdPaginated(id: String, page: Int, pageSize: Int): List<BikeRide> {
        return collection.find(BikeRide::userId eq id).sort(descending(BikeRide::dateTime)).skip(page*pageSize).limit(pageSize).toList()
    }

    override suspend fun getByUserIdPaginatedByKey(id: String, key: String?, pageSize: Int): List<BikeRide> {


        val query = if(key == null) {
            BikeRide::userId eq id
        } else {
            and(BikeRide::userId eq id, BikeRide::dateTime lt key )
        }
        return collection.find(query).sort(descending(BikeRide::dateTime)).limit(pageSize).toList()
    }

}