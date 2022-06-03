package com.bikenance.database.mongodb

import com.mongodb.client.MongoCollection
import org.bson.types.ObjectId
import org.litote.kmongo.deleteOneById
import org.litote.kmongo.findOneById
import org.litote.kmongo.updateOneById


interface BasicDao<T> {
    suspend fun getById(id: String): T?
    suspend  fun delete(id: String): Boolean
    suspend  fun create(item: T): T?
    suspend  fun update(id: String, item: T): Boolean
}

abstract class BasicDaoImpl<T : Any>(val collection: MongoCollection<T>) : BasicDao<T> {

    override suspend fun getById(id: String): T? {
        return collection.findOneById(ObjectId(id))
    }

    override suspend  fun delete(id: String): Boolean {
        return collection.deleteOneById(ObjectId(id)).deletedCount > 0
    }

    override suspend  fun create(item: T): T? {
        return collection.insertOne(item).let {
            it.insertedId?.let { id -> collection.findOneById(id) }
        }
    }

    override suspend  fun update(id: String, item: T): Boolean {
        return collection.updateOneById(ObjectId(id), item).matchedCount > 0
    }

}