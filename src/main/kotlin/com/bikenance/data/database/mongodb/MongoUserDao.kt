package com.bikenance.data.database.mongodb


import com.bikenance.data.database.StravaActivityDao
import com.bikenance.data.database.StravaAthleteDao
import com.bikenance.data.database.UserDao
import com.bikenance.data.model.User
import com.bikenance.data.model.UserUpdate
import com.bikenance.api.strava.AuthData
import com.bikenance.data.model.strava.StravaActivity
import com.bikenance.data.model.strava.StravaAthlete
import org.bson.types.ObjectId
import org.litote.kmongo.*

class MongoUserDao(private val db: DB) : BasicDaoImpl<User>(db.users), UserDao {

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
        return db.users.findOne(User::stravaAuthData / AuthData::accessToken eq token)
    }
}

class MongoStravaAthleteDao(private val db: DB) : BasicDaoImpl<StravaAthlete>(db.athletes), StravaAthleteDao {
    override suspend fun getByAthleteId(id: String): StravaAthlete? {
        return db.athletes.findOne(StravaAthlete::id eq id)
    }
}

class MongoStravaActivityDao(private val db: DB) : BasicDaoImpl<StravaActivity>(db.activities), StravaActivityDao {
    override suspend fun getByStravaId(id: String): StravaActivity? {
        return db.activities.findOne(StravaActivity::id eq id)
    }
}