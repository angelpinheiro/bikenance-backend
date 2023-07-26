package com.bikenance.usecase

import com.bikenance.data.database.mongodb.DAOS
import com.bikenance.data.database.mongodb.DB
import com.bikenance.data.model.Bike
import com.bikenance.data.model.User
import com.bikenance.data.model.strava.StravaAthlete
import com.bikenance.data.model.toBikeRide
import com.bikenance.data.network.push.MessageData
import com.bikenance.data.network.push.MessageSender
import com.bikenance.data.network.push.MessageType
import com.bikenance.data.network.strava.StravaApi
import com.bikenance.data.network.strava.StravaApiForUser
import com.bikenance.data.network.strava.StravaApiResponse
import com.bikenance.data.network.strava.supportedActivityTypes
import io.ktor.util.logging.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SyncStravaDataUseCase(
    val strava: StravaApi, val db: DB, val dao: DAOS, private val messageSender: MessageSender
) {

    val log = KtorSimpleLogger("UpdateRidesUseCase")
    private val scope = CoroutineScope(Job() + Dispatchers.IO)


    suspend operator fun invoke(userId: String) {
        val user = dao.userDao.getById(userId) ?: throw Exception("User not found")
        val auth = user.stravaAuthData ?: throw Exception("Auth data not found")
        val stravaClient = strava.withAuth(auth);
        invoke(userId, stravaClient)
    }

    suspend operator fun invoke(userId: String, stravaClient: StravaApiForUser) = scope.launch {
        val user = dao.userDao.getById(userId) ?: throw Exception("User not found")
        val profile = dao.profileDao.getByUserId(userId) ?: throw Exception("User profile not found")

        syncBikes(user, stravaClient)
        syncRides(user, stravaClient)

        dao.profileDao.update(profile.oid(), profile.copy(sync = true))

        dao.userDao.getById(userId)?.let {
            messageSender.sendMessage(
                it, MessageData(
                    MessageType.PROFILE_SYNC
                )
            )
        }
    }

    private suspend fun syncRides(user: User, stravaClient: StravaApiForUser) {

        var page = 1
        var keepGoing = true

        val bikes = dao.bikeDao.getByUserId(user.oid())

        // find the last saved ride, and only ask strava for rides after it
        val lastRide = dao.bikeRideDao.getLastByUserId(user.oid())

        // iterate over paginated strava activities until an empty page is returned
        // this is the recommended approach from strava api docs

        while (keepGoing) {

            val activities =
                stravaClient.activitiesPaginated(page, after = lastRide?.dateTime).successOrElse(emptyList())

            if (activities.isNotEmpty()) {
                log.info("Received ${activities.size} activities")
                activities.forEach { activity ->
                    log.info("-  ${activity.startDate} ${activity.name}")
                    val supported = supportedActivityTypes.contains(activity.type)
                    if (supported) {
                        val performedWith = bikes.firstOrNull { it.stravaId == activity.gearId }
                        db.activities.insertOne(activity)
                        dao.bikeRideDao.create(activity.toBikeRide(user, performedWith))
                    }
                }
                page = page.inc()
            } else {
                keepGoing = false
            }
        }
    }

    private suspend fun syncBikes(user: User, stravaClient: StravaApiForUser): List<Bike>? {

        val stravaAthlete: StravaAthlete = stravaClient.athlete().successOrFail("Could not get athlete")

        return stravaAthlete.bikeRefs?.mapNotNull { ref ->
            dao.bikeDao.getByStravaId(ref.id) ?: stravaClient.bike(ref.id).let { gear ->
                val newBike = when (gear) {
                    is StravaApiResponse.Success -> {
                        Bike(
                            name = ref.name,
                            brandName = gear.data.brandName,
                            modelName = gear.data.modelName,
                            distance = gear.data.distance,
                            userId = user.oid(),
                            stravaId = ref.id,
                            sync = true,
                            draft = true
                        )
                    }

                    else -> {
                        // If we cant get detailed gear info from strava,
                        // create a default bike with sync false for later update
                        Bike(
                            name = ref.name,
                            brandName = "",
                            modelName = "",
                            distance = 0,
                            userId = user.oid(),
                            stravaId = ref.id,
                            sync = false,
                            draft = true
                        )
                    }
                }
                dao.bikeDao.create(newBike)
            }

        }
    }

}