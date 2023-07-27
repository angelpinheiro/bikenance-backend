package com.bikenance.usecase.strava

import com.bikenance.data.database.mongodb.DAOS
import com.bikenance.data.model.BikeRide
import com.bikenance.data.model.User
import com.bikenance.data.model.strava.AspectType
import com.bikenance.data.model.strava.EventData
import com.bikenance.data.model.strava.ObjectType
import com.bikenance.data.model.toBikeRide
import com.bikenance.data.network.push.MessageData
import com.bikenance.data.network.push.MessageSender
import com.bikenance.data.network.push.MessageType
import com.bikenance.data.network.strava.StravaApi
import com.bikenance.data.network.strava.supportedActivityTypes
import com.bikenance.data.repository.UserRepository
import com.bikenance.util.bknLogger


class StravaEventReceivedUseCase(
    private val userRepository: UserRepository,
    private val dao: DAOS,
    private val strava: StravaApi,
    private val messageSender: MessageSender
) {

    private val log = bknLogger("StravaEventReceivedUseCase")

    suspend operator fun invoke(eventData: EventData) {
        log.info("Event data $eventData")
        userRepository.getByAthleteId(eventData.ownerId)?.let { user ->
            log.debug("Event: ${eventData.aspectType.type} ${eventData.objectType.type}")
            when (eventData.objectType) {
                ObjectType.ACTIVITY -> {
                    when (eventData.aspectType) {
                        AspectType.CREATE -> handleActivityCreate(user, eventData)
                        AspectType.UPDATE -> handleActivityUpdate(user, eventData)
                        AspectType.DELETE -> handleActivityDelete(user, eventData)
                    }
                }

                ObjectType.ATHLETE -> {
                    println("Received athlete update: $eventData")
                }
            }
        }
    }

    private suspend fun handleActivityCreate(user: User, eventData: EventData) {

        val api = strava.withAuth(user.stravaAuthData ?: throw Exception("User auth not found"))
        val activity = api.activity(eventData.objectId).successOrFail("Could not get activity from API")

        if (supportedActivityTypes.contains(activity.type)) {
            val bike = activity.gearId?.let { stravaBikeId ->
                dao.bikeDao.getByStravaId(stravaBikeId)
            }
            dao.stravaActivityDao.create(activity)
            val createdBikeRide = dao.bikeRideDao.create(activity.toBikeRide(user, bike, false))
            if (createdBikeRide != null) {
                sendNewRideMessage(user, createdBikeRide)
            } else {
                log.error("Could not save new ride $activity")
            }
        }
    }

    private fun sendNewRideMessage(user: User, createdBikeRide: BikeRide) {
        messageSender.sendMessage(
            user, MessageData(
                MessageType.NEW_ACTIVITY, mapOf(
                    "id" to createdBikeRide.oid(),
                )
            )
        )
    }

    private suspend fun handleActivityUpdate(user: User, eventData: EventData) {
        TODO("Not implemented yet")
    }

    private suspend fun handleActivityDelete(user: User, eventData: EventData) {
        TODO("Not implemented yet")
    }


}

