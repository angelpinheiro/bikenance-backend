package com.bikenance.database.mongodb


import com.bikenance.database.UserDaoFacade
import com.bikenance.features.strava.AuthData
import com.bikenance.model.User
import org.litote.kmongo.*

class MongoUserDao(private val db: DB) : UserDaoFacade {
    override suspend fun findById(id: String): User? {
        return db.users.findOneById(id)
    }

    override suspend fun user(username: String): User? {
        return db.users.findOne(User::username eq username)
    }

    override suspend fun findByAthleteId(athleteId: String): User? {
        return db.users.findOne(User::athleteId eq athleteId)
    }

    override suspend fun allUsers(): List<User> {
        return db.users.find().toList()
    }

    override suspend fun filter(pattern: String): List<User> {
        return db.users.find(User::username regex "*$pattern*").toList()
    }

    override suspend fun createUser(username: String, password: String): User? {
        return db.users.insertOne(User(username = username, password = password)).let {
            it.insertedId?.let { id -> db.users.findOneById(id) }
        }
    }

    override suspend fun createUser(user: User): User? {
        return db.users.insertOne(user).let {
            it.insertedId?.let { id -> db.users.findOneById(id) }
        }
    }

    override suspend fun updateUser(id: String, user: User): Boolean {
        return db.users.updateOneById(id, user).modifiedCount > 0
    }

    override suspend fun deleteUser(id: String): Boolean {
        return db.users.deleteOneById(id).deletedCount > 0
    }

    override suspend fun findByToken(token: String): User? {
        return db.users.findOne(User::authData / AuthData::accessToken eq token)
    }
}