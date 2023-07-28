package com.bikenance.usecase.strava

import com.bikenance.data.database.mongodb.DAOS
import com.bikenance.data.model.Bike
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
import com.bikenance.util.updateWear


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

            // update bike wear
            if (bike != null && createdBikeRide != null) {
                log.info("Updating wear of bike [${bike.oid()}] [${bike.name}] with [${createdBikeRide.name}]")
                val updatedBike = updateBikeWear(bike, createdBikeRide)
                log.info("Updated bike [${updatedBike}]")
                val result = dao.bikeDao.update(updatedBike.oid(), updatedBike)
                log.info("Updated result [${result}]")
            }

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


    private fun updateBikeWear(bike: Bike, ride: BikeRide): Bike {

        val distance = ride.distance ?: 0
        val movingTime = ride.elapsedTime ?: 0
        val elevationGain = ride.totalElevationGain ?: 0

        val updatedComponents = bike.components?.map { bikeComponent ->
            // update maintenance usage and wear
            val updatedMaintenances = bikeComponent.maintenance?.map { m ->
                val maintenance = m.copy(
                    usageSinceLast = m.usageSinceLast.plus(movingTime, distance, elevationGain)
                )
                maintenance.updateWear(ride.dateTime)
            }
            // update component usage
            bikeComponent.copy(
                usage = bikeComponent.usage.plus(movingTime, distance, elevationGain),
                maintenance = updatedMaintenances
            )
        }

        updatedComponents?.forEach { c ->
            log.info("Component ${c.type} has an usage of ${c.usage.distance}")
            c.maintenance?.forEach { m ->
                log.info("     - Maintenance ${m.type} has an usage of ${m.usageSinceLast.distance}")
            }
        }

        return bike.copy(
            name = bike.name + " *",
            components = updatedComponents
        )
    }


}

