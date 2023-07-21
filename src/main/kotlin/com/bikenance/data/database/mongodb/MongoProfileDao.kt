package com.bikenance.data.database.mongodb

import com.bikenance.data.database.ProfileDao
import com.bikenance.data.model.Profile
import org.litote.kmongo.eq
import org.litote.kmongo.findOne

class MongoProfileDao(db: DB) : BasicDaoImpl<Profile, Unit>(db.profiles), ProfileDao {

    override suspend fun getByUserId(id: String): Profile? {
        return collection.findOne(Profile::userId eq id)
    }

}


