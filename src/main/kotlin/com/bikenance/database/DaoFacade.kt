package com.bikenance.database

import com.bikenance.database.mongodb.IBasicDao
import com.bikenance.model.*

interface UserDao : IBasicDao<User> {
    suspend fun getByUsername(username: String): User?
    suspend fun getByAthleteId(athleteId: String): User?
    suspend fun findAll(): List<User>
    suspend fun filter(pattern: String): List<User>
    suspend fun create(title: String, body: String): User?
    suspend fun update(id: String, user: UserUpdate): Boolean
    suspend fun getByAccessToken(token: String): User?

}


interface ProfileDao : IBasicDao<Profile> {
    suspend fun getByUserId(id: String): Profile?
}

interface BikeDao : IBasicDao<Bike> {
    suspend fun getByStravaId(id: String): Bike?
    suspend fun getByUserId(id: String): List<Bike>
}

interface BikeRideDao : IBasicDao<BikeRide> {
    suspend fun getByStravaId(id: String): BikeRide?
    suspend fun getByBikeId(id: String): BikeRide?
    suspend fun getByUserId(id: String): List<BikeRide>

}


