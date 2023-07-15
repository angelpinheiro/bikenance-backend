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
import com.bikenance.data.network.stravaApi.Strava
import com.bikenance.data.network.stravaApi.supportedActivityTypes
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



    fun syncRides(user: User, bike: Bike) {
        scope.launch {

            val stravaClient = strava.withAuth(user.stravaAuthData ?: throw Exception("User auth not found"));

            var page = 1
            var keepGoing = true
            var createdCount = 0

            // iterate over paginated strava activities until an empty page is returned
            // this is the recommended approach from strava api docs
            while (keepGoing) {
                val activities = stravaClient.activitiesPaginated(page)
                if (activities?.isNotEmpty() == true) {
                    createdCount += activities.sumOf { activity ->
                        createRideIfNotExist(user, bike, activity)
                    }
                    page +=1
                } else {
                    keepGoing = false
                }
            }

            if (createdCount > 0) {
                // notify client
                sendCreatedRidesMessage(user, createdCount)
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