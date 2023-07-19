package com.bikenance.usecase

import com.bikenance.data.database.mongodb.DAOS
import com.bikenance.data.database.mongodb.DB
import com.bikenance.data.model.Bike
import com.bikenance.data.model.User
import com.bikenance.data.model.toBikeRide
import com.bikenance.data.network.push.MessageData
import com.bikenance.data.network.push.MessageSender
import com.bikenance.data.network.push.MessageType
import com.bikenance.data.repository.UserRepository
import com.bikenance.data.network.strava.StravaApiForUser
import com.bikenance.data.model.strava.StravaAthlete
import com.bikenance.data.network.strava.Strava
import com.bikenance.data.network.strava.supportedActivityTypes
import io.ktor.util.logging.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SyncStravaDataUseCase(
    val strava: Strava,
    val db: DB,
    val dao: DAOS,
    val userRepository: UserRepository,
    private val messageSender: MessageSender
) {

    val log = KtorSimpleLogger("UpdateRidesUseCase")
    private val scope = CoroutineScope(Job() + Dispatchers.IO)

    suspend operator fun invoke(userId: String, stravaClient: StravaApiForUser) = scope.launch {
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
                    val supported = supportedActivityTypes.contains(activity.type)
                    if(supported) {
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

        val stravaAthlete: StravaAthlete = stravaClient.athlete() ?: throw Exception("Could not get athlete info")
        log.debug("syncBikes: ${stravaAthlete.bikeRefs?.size} bike refs")
        return stravaAthlete.bikeRefs?.mapNotNull { ref ->
            stravaClient.bike(ref.id)?.let { gear ->
                log.debug("syncBikes: sync bike ${gear.name}")
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