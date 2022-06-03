package com.bikenance.database.mongodb

import com.bikenance.database.ProfileDao
import com.bikenance.model.Profile
import org.litote.kmongo.eq
import org.litote.kmongo.findOne

class MongoProfileDao(db: DB) : BasicDaoImpl<Profile>(db.profiles), ProfileDao {

    override suspend fun getByUserId(id: String): Profile? {
        return collection.findOne(Profile::userId eq id)
    }

}


