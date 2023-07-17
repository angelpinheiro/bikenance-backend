package com.bikenance.usecase.strava

import com.bikenance.data.database.mongodb.DAOS
import com.bikenance.data.database.mongodb.DB
import com.bikenance.data.model.Bike
import com.bikenance.data.model.BikeRide
import com.bikenance.data.model.User
import com.bikenance.data.model.toBikeRide
import com.bikenance.data.network.push.MessageData
import com.bikenance.data.network.push.MessageSender
import com.bikenance.data.network.push.MessageType
import com.bikenance.data.model.strava.StravaActivity
import com.bikenance.data.network.strava.Strava
import com.bikenance.data.network.strava.supportedActivityTypes
import io.ktor.util.logging.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.litote.kmongo.eq
import org.litote.kmongo.findOne


class StravaBikeSync(
    private val strava: Strava,
    private val db: DB,
    private val dao: DAOS,
    private val messageSender: MessageSender
) {

    private val log = KtorSimpleLogger("StravaBikeSync")
    private val scope = CoroutineScope(Job() + Dispatchers.IO)


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


    private suspend fun createRideIfNotExist(user: User, bike: Bike, stravaActivity: StravaActivity): Int {
        if (db.activities.findOne(StravaActivity::id eq stravaActivity.id) == null) {
            val supported = supportedActivityTypes.contains(stravaActivity.type)
            if (bike.stravaId == stravaActivity.gearId && supported) {
                // save strava activity on db
                db.activities.insertOne(stravaActivity)
                // create a ride from the strava activity and save it
                dao.bikeRideDao.create(
                    stravaActivity.toBikeRide(user, bike)
                )
                return 1
            }
        }
        return 0
    }

    private suspend fun sendCreatedRidesMessage(user: User, count: Int) {
        messageSender.sendMessage(
            user, MessageData(
                MessageType.RIDES_UPDATED, mapOf(
                    "count" to count.toString()
                )
            )
        )
    }
}