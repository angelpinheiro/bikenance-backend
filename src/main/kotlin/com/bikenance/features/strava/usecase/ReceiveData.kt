package com.bikenance.features.strava.usecase

import com.bikenance.database.mongodb.DAOS
import com.bikenance.features.firebase.MessageData
import com.bikenance.features.firebase.MessageSender
import com.bikenance.features.firebase.MessageType
import com.bikenance.features.strava.api.Strava
import com.bikenance.features.strava.api.supportedActivityTypes
import com.bikenance.features.strava.model.AspectType
import com.bikenance.features.strava.model.EventData
import com.bikenance.features.strava.model.ObjectType
import com.bikenance.model.BikeRide
import com.bikenance.model.User
import com.bikenance.model.toBikeRide
import com.bikenance.repository.UserRepository


class ReceiveDataUseCase(
    private val userRepository: UserRepository,
    private val dao: DAOS,
    private val strava: Strava,
    private val messageSender: MessageSender
) {

    suspend fun handleEventData(eventData: EventData) {
        println("Event data $eventData")
        userRepository.getByAthleteId(eventData.ownerId)?.let { user ->
            println("Event: ${eventData.aspectType.type} ${eventData.objectType.type}")
            when (eventData.objectType) {
                ObjectType.ACTIVITY -> {
                    when (eventData.aspectType) {
                        AspectType.CREATE -> {
                            handleActivityCreate(user, eventData)
                        }
                        AspectType.UPDATE -> {
                            handleActivityUpdate(user, eventData)
                        }
                        AspectType.DELETE -> {
                            handleActivityDelete(user, eventData)
                        }
                    }
                }
                ObjectType.ATHLETE -> {
                    println("Received athlete update: $eventData")
                }
            }
        }
    }

    private suspend fun handleActivityCreate(user: User, eventData: EventData) {
        println("handleActivityCreate")
        user.authData?.let { authData ->
            strava.withAuth(authData).activity(eventData.objectId)?.let { activity ->
                val supported = supportedActivityTypes.contains(activity.type)
                if (supported) {
                    val bike = activity.gearId?.let { dao.bikeDao.getByStravaId(it) }
                    dao.stravaActivityDao.create(activity)
                    dao.bikeRideDao.create(
                        activity.toBikeRide(user, bike)
                    )?.let {
                        messageSender.sendMessage(
                            user, MessageData(
                                MessageType.NEW_ACTIVITY, mapOf(
                                    "id" to it.oid(),
                                )
                            )
                        )
                    }
                }
            }
        }
    }

    private suspend fun handleActivityUpdate(user: User, eventData: EventData) {
        println("handleActivityUpdate")
        user.authData?.let {
            strava.withAuth(it).activity(eventData.objectId)?.let { new ->
                when (val current = dao.stravaActivityDao.getByStravaId(eventData.objectId)) {
                    null -> dao.stravaActivityDao.create(new)
                    else -> dao.stravaActivityDao.update(current.oid(), new)
                }
            }
        }
    }

    private suspend fun handleActivityDelete(user: User, eventData: EventData) {
        TODO("Not implemented yet")
    }


}

