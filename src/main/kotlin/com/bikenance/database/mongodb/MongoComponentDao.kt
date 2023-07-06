package com.bikenance.database.mongodb

import com.bikenance.database.ComponentDao
import com.bikenance.model.components.BikeComponent
import org.litote.kmongo.eq

class MongoComponentDao(db: DB) : BasicDaoImpl<BikeComponent>(db.components), ComponentDao {
    override suspend fun getByBikeId(id: String): List<BikeComponent> {
        return collection.find(BikeComponent::bikeId eq id).toList()
    }

}