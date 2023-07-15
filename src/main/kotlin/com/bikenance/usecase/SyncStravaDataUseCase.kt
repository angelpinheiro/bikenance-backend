package com.bikenance.usecase

import com.bikenance.database.mongodb.DAOS
import com.bikenance.database.mongodb.DB
import com.bikenance.model.Bike
import com.bikenance.model.User
import com.bikenance.model.toBikeRide
import com.bikenance.push.MessageData
import com.bikenance.push.MessageSender
import com.bikenance.push.MessageType
import com.bikenance.repository.UserRepository
import com.bikenance.strava.api.StravaApiForUser
import com.bikenance.strava.model.StravaAthlete
import io.ktor.util.logging.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SyncStravaDataUseCase(
    val strava: com.bikenance.strava.api.Strava,
    val db: DB,
    val dao: DAOS,
    val userRepository: UserRepository,
    private val messageSender: MessageSender
) {

    val log = KtorSimpleLogger("UpdateRidesUseCase")
    private val scope = CoroutineScope(Job() + Dispatchers.IO)

    suspend fun syncBikesAndRides(userId: String, stravaClient: StravaApiForUser) = scope.launch {
        val user = dao.userDao.getById(userId) ?: throw Exception("User not found")
        val profile = dao.profileDao.getByUserId(userId) ?: throw Exception("User profile not found")

        syncBikes(user, stravaClient)
        syncRides(user, stravaClient)

        dao.profileDao.update(profile.oid(), profile.copy(sync = true))

        messageSender.sendMessage(
            user, MessageData(
                MessageType.PROFILE_SYNC
            )
        )
    }

    private suspend fun syncRides(user: User, stravaClient: StravaApiForUser) {

        var page = 1
        var keepGoing = true

        val bikes = dao.bikeDao.getByUserId(user.oid())

        // iterate over paginated strava activities until an empty page is returned
        // this is the recommended approach from strava api docs

        while (keepGoing) {
            val activities = stravaClient.activitiesPaginated(page) ?: emptyList()
            if (activities.isNotEmpty()) {

                activities.forEach { activity ->
                    val performedWith = bikes.firstOrNull { it.stravaId == activity.gearId }
                    db.activities.insertOne(activity)
                    dao.bikeRideDao.create(activity.toBikeRide(user, performedWith))
                }
                page = page.inc()
            } else {
                keepGoing = false
            }
        }
    }

    private suspend fun syncBikes(user: User, stravaClient: StravaApiForUser): List<Bike>? {

        val stravaAthlete: StravaAthlete = stravaClient.athlete() ?: throw Exception("Could not get athlete info")

        return stravaAthlete.bikeRefs?.mapNotNull { ref ->
            stravaClient.bike(ref.id)?.let { gear ->
                val bike = Bike(
                    name = ref.name,
                    brandName = gear.brandName,
                    modelName = gear.modelName,
                    distance = gear.distance,
                    userId = user.oid(),
                    stravaId = ref.id,
                    draft = true
                )
                dao.bikeDao.create(bike)
            }
        }
    }

}