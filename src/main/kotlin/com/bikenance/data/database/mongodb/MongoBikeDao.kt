package com.bikenance.data.database.mongodb

import com.bikenance.data.database.BikeDao
import com.bikenance.data.model.Bike
import org.bson.types.ObjectId
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import org.litote.kmongo.setValue

class MongoBikeDao(db: DB) : BasicDaoImpl<Bike>(db.bikes), BikeDao {
    override suspend fun getByStravaId(id: String): Bike? {
        return collection.findOne(Bike::stravaId eq id)
    }

    override suspend fun getByUserId(id: String): List<Bike> {
        return collection.find(Bike::userId eq id).toList()
    }

    override suspend fun updateSyncStatus(bikeId: String, sync: Boolean): Boolean {
        return updateById(bikeId, setValue(Bike::draft, !sync))
    }

}