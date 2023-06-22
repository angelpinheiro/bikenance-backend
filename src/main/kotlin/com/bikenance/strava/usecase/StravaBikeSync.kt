package com.bikenance.strava.usecase

import com.bikenance.database.mongodb.DAOS
import com.bikenance.database.mongodb.DB
import com.bikenance.model.Bike
import com.bikenance.model.BikeRide
import com.bikenance.model.User
import com.bikenance.model.toBikeRide
import com.bikenance.push.MessageData
import com.bikenance.push.MessageSender
import com.bikenance.push.MessageType
import com.bikenance.strava.model.StravaActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import java.time.LocalDateTime


class StravaBikeSync(
    private val strava: com.bikenance.strava.api.Strava,
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
                    val supported = com.bikenance.strava.api.supportedActivityTypes.contains(stravaActivity.type)
                    if (bike.stravaId == stravaActivity.gearId && supported) {
                        modifiedCount++
                        // save strava activity on db
                        db.activities.insertOne(stravaActivity)
                        // create a ride from the strava activity and save it
                        dao.bikeRideDao.create(
                            stravaActivity.toBikeRide(user, bike)
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