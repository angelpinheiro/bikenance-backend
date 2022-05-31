package com.bikenance.database.mongodb


import com.bikenance.database.UserDaoFacade
import com.bikenance.features.strava.AuthData
import com.bikenance.model.User
import org.litote.kmongo.*

class MongoUserDao(private val db: DB) : UserDaoFacade {
    override suspend fun getById(id: String): User? {
        return db.users.findOneById(id)
    }

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

    override suspend fun create(user: User): User? {
        return db.users.insertOne(user).let {
            it.insertedId?.let { id -> db.users.findOneById(id) }
        }
    }

    override suspend fun update(id: String, user: User): Boolean {
        return db.users.updateOneById(id, user).modifiedCount > 0
    }

    override suspend fun delete(id: String): Boolean {
        return db.users.deleteOneById(id).deletedCount > 0
    }

    override suspend fun getByAccessToken(token: String): User? {
        return db.users.findOne(User::authData / AuthData::accessToken eq token)
    }
}