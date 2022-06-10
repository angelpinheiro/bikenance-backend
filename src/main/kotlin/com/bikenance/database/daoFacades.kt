package com.bikenance.database

import com.bikenance.database.mongodb.BasicDao
import com.bikenance.features.strava.model.StravaActivity
import com.bikenance.features.strava.model.StravaAthlete
import com.bikenance.model.*

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
    suspend fun getByUserIdAndBikeId(userId: String, bikeId: String): Bike?
}

interface BikeRideDao : BasicDao<BikeRide> {
    suspend fun getByStravaId(id: String): BikeRide?
    suspend fun getByBikeId(id: String): BikeRide?
    suspend fun getByUserId(id: String): List<BikeRide>

}

interface StravaAthleteDao : BasicDao<StravaAthlete> {
    suspend fun getByAthleteId(id: String): StravaAthlete?
}

interface StravaActivityDao : BasicDao<StravaActivity>

