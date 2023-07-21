package com.bikenance.data.database.mongodb

import com.bikenance.data.database.BikeRideDao
import com.bikenance.data.model.BikeRide
import com.mongodb.client.FindIterable
import org.litote.kmongo.*
import java.time.LocalDateTime

class MongoBikeRideDao(db: DB) : BasicDaoImpl<BikeRide, Unit>(db.bikeRides), BikeRideDao {

    override suspend fun getByStravaId(id: String): BikeRide? {
        return collection.findOne(BikeRide::stravaId eq id)
    }

    override suspend fun getByBikeId(id: String): Iterable<BikeRide> {
        return collection.find(BikeRide::bikeId eq id)
    }

    override suspend fun getByBikeIdAfter(id: String, date: LocalDateTime): List<BikeRide> {
        val q = and(BikeRide::bikeId eq id, BikeRide::dateTime gt date)
        return collection.find(q).toList()
    }

    override suspend fun getByUserId(id: String): List<BikeRide> {
        return collection.find(BikeRide::userId eq id).toList()
    }

    override suspend fun getByUserIdPaginated(id: String, page: Int, pageSize: Int): List<BikeRide> {
        return collection.find(BikeRide::userId eq id).sort(descending(BikeRide::dateTime)).skip(page * pageSize)
            .limit(pageSize).toList()
    }

    override suspend fun getByUserIdPaginatedByKey(id: String, before: LocalDateTime?, pageSize: Int): List<BikeRide> {


        val query = if (before == null) {
            BikeRide::userId eq id
        } else {
            and(BikeRide::userId eq id, BikeRide::dateTime lt before)
        }
        return collection.find(query).sort(descending(BikeRide::dateTime)).limit(pageSize).toList()
    }

}