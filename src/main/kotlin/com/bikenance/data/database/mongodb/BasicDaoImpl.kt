package com.bikenance.data.database.mongodb

import com.mongodb.client.MongoCollection
import com.mongodb.client.model.UpdateOptions
import org.bson.conversions.Bson
import org.bson.types.ObjectId
import org.litote.kmongo.Id
import org.litote.kmongo.deleteOneById
import org.litote.kmongo.findOneById
import org.litote.kmongo.updateOneById


abstract class MongoModel<T> {

    abstract val _id: Id<T>

    fun oid(): String {
        return _id.toString()
    }
}

interface BasicDao<T, U> {
    suspend fun all(): Iterable<T>
    suspend fun getById(id: String): T?
    suspend fun delete(id: String): Boolean
    suspend fun create(item: T): T?
    suspend fun update(id: String, item: T): Boolean
    suspend fun partialUpdate(id: String, item: U): Boolean
    suspend fun updateById(id: String, update: Bson): Boolean
}

abstract class BasicDaoImpl<T : MongoModel<T>, U : Any>(val collection: MongoCollection<T>) : BasicDao<T, U> {

    override suspend fun all(): Iterable<T> {
        return collection.find()
    }

    override suspend fun getById(id: String): T? {
        return collection.findOneById(ObjectId(id))
    }

    override suspend fun delete(id: String): Boolean {
        return collection.deleteOneById(ObjectId(id)).deletedCount > 0
    }

    override suspend fun create(item: T): T? {
        return collection.insertOne(item).let {
            collection.findOneById(item._id)
        }
    }

    override suspend fun partialUpdate(id: String, item: U): Boolean {
        return collection.updateOneById(ObjectId(id), item).matchedCount > 0
    }

    override suspend fun update(id: String, item: T): Boolean {
        return collection.updateOneById(ObjectId(id), item).matchedCount > 0
    }

    override suspend fun updateById(id: String, update: Bson): Boolean {
        return collection.updateOneById(ObjectId(id), update, UpdateOptions()).matchedCount > 0
    }

}