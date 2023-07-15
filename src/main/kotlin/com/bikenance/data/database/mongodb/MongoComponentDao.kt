package com.bikenance.data.database.mongodb

import com.bikenance.data.database.ComponentDao
import com.bikenance.data.model.components.BikeComponent
import org.litote.kmongo.eq

class MongoComponentDao(db: DB) : BasicDaoImpl<BikeComponent>(db.components), ComponentDao {
    override suspend fun getByBikeId(id: String): List<BikeComponent> {
        return collection.find(BikeComponent::bikeId eq id).toList()
    }

}