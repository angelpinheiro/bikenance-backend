package com.bikenance.database.mongodb

import com.bikenance.database.BikeDao
import com.bikenance.database.ComponentDao
import com.bikenance.model.Bike
import com.bikenance.model.Component
import org.litote.kmongo.eq
import org.litote.kmongo.findOne

class MongoComponentDao(db: DB) : BasicDaoImpl<Component>(db.components), ComponentDao {
    override suspend fun getByBikeId(id: String): List<Component> {
        return collection.find(Component::bikeId eq id).toList()
    }

}