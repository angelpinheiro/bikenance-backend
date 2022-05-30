package com.bikenance.database.mongodb

import com.bikenance.database.UserDao
import com.bikenance.database.UserDaoFacade
import com.bikenance.model.User
import com.bikenance.model.UserUpdate
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import org.litote.kmongo.findOneById
import org.litote.kmongo.regex

class MongoUserDao(private val db: DB) : UserDaoFacade {
    override suspend fun user(id: Int): User? {
        return db.users.findOne(User::id eq id)
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
            db.users.findOneById(it.insertedId)
        }
    }

    override suspend fun createUser(user: User): User? {
        return db.users.insertOne(user).let {
            db.users.findOneById(it.insertedId)
        }
    }

    override suspend fun editUser(id: Int, user: UserUpdate): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun deleteUser(id: Int): Boolean {
        return db.users.deleteOne(User::id eq id).deletedCount > 0
    }
}