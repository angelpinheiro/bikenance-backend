package com.bikenance.features.strava.usecase

import com.bikenance.database.mongodb.DAOS
import com.bikenance.database.mongodb.DB
import com.bikenance.features.firebase.MessageData
import com.bikenance.features.firebase.MessageSender
import com.bikenance.features.firebase.MessageType
import com.bikenance.features.strava.api.Strava
import com.bikenance.features.strava.api.supportedActivityTypes
import com.bikenance.features.strava.model.StravaActivity
import com.bikenance.model.Bike
import com.bikenance.model.BikeRide
import com.bikenance.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import java.time.LocalDateTime


class StravaBikeSync(
    private val strava: Strava,
    private val db: DB,
    private val dao: DAOS,
    private val messageSender: MessageSender
) {

    private val scope = CoroutineScope(Job() + Dispatchers.IO)

    fun onBikeAdded(user: User, bike: Bike) {
        scope.launch {

            var modifiedCount = 0

            val stravaClient = strava.withAuth(user.authData ?: throw Exception("User auth not found"));
            // find activities in the previous six months
            stravaClient.activities(LocalDateTime.now().minusMonths(6))?.forEach { stravaActivity ->
                // ensure they are not already on db
                if (db.activities.findOne(StravaActivity::id eq stravaActivity.id) == null) {
                    val supported = supportedActivityTypes.contains(stravaActivity.type)
                    if (bike.stravaId == stravaActivity.gearId && supported) {
                        modifiedCount++
                        // save strava activity on db
                        db.activities.insertOne(stravaActivity)
                        // create a ride from the strava activity and save it
                        dao.bikeRideDao.create(
                            BikeRide(
                                userId = user.oid(),
                                stravaId = stravaActivity.id,
                                bikeId = bike.oid(),
                                name = stravaActivity.name,
                                distance = stravaActivity.distance,
                                movingTime = stravaActivity.movingTime,
                                elapsedTime = stravaActivity.elapsedTime,
                                dateTime = stravaActivity.startDate,
                                totalElevationGain = stravaActivity.totalElevationGain,
                            )
                        )
                    }
                }
            }
            if (modifiedCount > 0) {

                println("Sending message")
                // notify client
                messageSender.sendMessage(
                    user, MessageData(
                        MessageType.RIDES_UPDATED, mapOf(
                            "count" to modifiedCount.toString()
                        )
                    )
                )
            }
        }
    }

    fun onBikeRemoved(user: User, bike: Bike) {
        scope.launch {
            // delete rides from the bike being removed
            db.bikeRides.deleteMany(BikeRide::bikeId eq bike.oid())
            // delete activities from the bike being removed
            db.activities.deleteMany(StravaActivity::gearId eq bike.stravaId)

            println("Sending message")
            // notify client
            messageSender.sendMessage(
                user, MessageData(
                    MessageType.RIDES_DELETED
                )
            )
        }

    }
}