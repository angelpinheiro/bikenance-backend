package com.bikenance.database.mongodb


import com.bikenance.database.UserDao
import com.bikenance.features.strava.AuthData
import com.bikenance.model.Profile
import com.bikenance.model.User
import com.bikenance.model.UserUpdate
import com.mongodb.client.model.UpdateOptions
import org.bson.types.ObjectId

import org.litote.kmongo.*

class MongoUserDao(private val db: DB) : BasicDao<User>(db.users), UserDao {

    override suspend fun getByUsername(username: String): User? {
        return db.users.findOne(User::username eq username)
    }

    override suspend fun getByAthleteId(athleteId: String): User? {
        return db.users.findOne(User::athleteId eq athleteId)
    }

    override suspend fun findAll(): List<User> {
        return db.users.find().toList()
    }

    override suspend fun filter(pattern: String): List<User> {
        return db.users.find(User::username regex "*$pattern*").toList()
    }

    override suspend fun create(username: String, password: String): User? {
        return db.users.insertOne(User(username = username, password = password)).let {
            it.insertedId?.let { id -> db.users.findOneById(id) }
        }
    }

    override suspend fun update(id: String, user: UserUpdate): Boolean {
        return db.users.updateOneById(ObjectId(id), user).matchedCount > 0
    }


    override suspend fun getByAccessToken(token: String): User? {
        return db.users.findOne(User::authData / AuthData::accessToken eq token)
    }
}