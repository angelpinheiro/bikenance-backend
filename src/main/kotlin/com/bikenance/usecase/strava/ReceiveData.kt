package com.bikenance.usecase.strava

import com.bikenance.data.database.mongodb.DAOS
import com.bikenance.data.model.User
import com.bikenance.data.model.toBikeRide
import com.bikenance.data.network.push.MessageData
import com.bikenance.data.network.push.MessageSender
import com.bikenance.data.network.push.MessageType
import com.bikenance.data.repository.UserRepository
import com.bikenance.data.model.strava.AspectType
import com.bikenance.data.model.strava.EventData
import com.bikenance.data.model.strava.ObjectType
import com.bikenance.data.network.stravaApi.Strava
import com.bikenance.data.network.stravaApi.supportedActivityTypes


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
        user.stravaAuthData?.let { authData ->
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
        user.stravaAuthData?.let {
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

