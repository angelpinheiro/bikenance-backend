package com.bikenance.data.database

import com.bikenance.data.database.mongodb.BasicDao
import com.bikenance.data.model.*
import com.bikenance.data.model.components.BikeComponent
import com.bikenance.data.model.strava.StravaActivity
import com.bikenance.data.model.strava.StravaAthlete
import java.time.LocalDateTime

interface UserDao : BasicDao<User> {
    suspend fun getByUsername(username: String): User?
    suspend fun getByAthleteId(athleteId: String): User?
    suspend fun findAll(): List<User>
    suspend fun filter(pattern: String): List<User>
    suspend fun create(title: String, body: String): User?
    suspend fun update(id: String, user: UserUpdate): Boolean
    suspend fun getByAccessToken(token: String): User?

}


interface ProfileDao : BasicDao<Profile> {
    suspend fun getByUserId(id: String): Profile?
}


interface BikeDao : BasicDao<Bike> {
    suspend fun getByStravaId(id: String): Bike?
    suspend fun getByUserId(id: String): List<Bike>
    suspend fun updateSyncStatus(bikeId: String, sync: Boolean): Boolean
}

interface BikeRideDao : BasicDao<BikeRide> {
    suspend fun getByStravaId(id: String): BikeRide?
    suspend fun getByBikeId(id: String): BikeRide?
    suspend fun getByBikeIdAfter(id: String, date: LocalDateTime): List<BikeRide>
    suspend fun getByUserId(id: String): List<BikeRide>
    suspend fun getByUserIdPaginated(id: String, page: Int, pageSize: Int): List<BikeRide>
    suspend fun getByUserIdPaginatedByKey(id: String, key: String?, pageSize: Int): List<BikeRide>
}

interface ComponentDao : BasicDao<BikeComponent> {
    suspend fun getByBikeId(id: String): List<BikeComponent>
}


interface StravaAthleteDao : BasicDao<StravaAthlete> {
    suspend fun getByAthleteId(id: String): StravaAthlete?
}

interface StravaActivityDao : BasicDao<StravaActivity> {
    suspend fun getByStravaId(id: String): StravaActivity?
}
