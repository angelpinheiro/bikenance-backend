package com.bikenance.features.strava.usecase

import com.bikenance.database.mongodb.DB
import com.bikenance.features.strava.api.Strava
import com.bikenance.features.strava.model.AspectType
import com.bikenance.features.strava.model.EventData
import com.bikenance.features.strava.model.ObjectType
import com.bikenance.features.strava.model.StravaActivity
import com.bikenance.model.User
import com.bikenance.repository.UserRepository
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import org.litote.kmongo.updateOneById


class ReceiveDataUseCase(private val userRepository: UserRepository) {

    suspend fun handleEventData(db: DB, strava: Strava, eventData: EventData) {
        println("Event data $eventData")
        userRepository.getByAthleteId(eventData.ownerId)?.let { user ->
            println("Event: ${eventData.aspectType.type} ${eventData.objectType.type}")
            when (eventData.objectType) {
                ObjectType.ACTIVITY -> {
                    when (eventData.aspectType) {
                        AspectType.CREATE -> {
                            handleActivityCreate(user, eventData, strava, db)
                        }
                        AspectType.UPDATE -> {
                            handleActivityUpdate(user, eventData, strava, db)
                        }
                        AspectType.DELETE -> {
                            handleActivityDelete(user, eventData, strava, db)
                        }
                    }
                }
                ObjectType.ATHLETE -> {
                    println("Received athlete update: $eventData")
                }
            }
        }
    }

    private suspend fun handleActivityCreate(user: User, eventData: EventData, strava: Strava, db: DB) {
        println("handleActivityCreate")
        user.authData?.let {
            val activity = strava.withAuth(it).activity(eventData.objectId)
            db.activities.insertOne(activity)
        }
    }

    private suspend fun handleActivityUpdate(user: User, eventData: EventData, strava: Strava, db: DB) {
        println("handleActivityUpdate")
        user.authData?.let {
            strava.withAuth(it).activity(eventData.objectId)?.let { new ->
                when (val current = db.activities.findOne(StravaActivity::id eq eventData.objectId)) {
                    null -> db.activities.insertOne(new)
                    else -> db.activities.updateOneById(current._id, new)
                }
            }
        }
    }

    private suspend fun handleActivityDelete(user: User, eventData: EventData, strava: Strava, db: DB) {
        TODO("Not implemented yet")
    }


}

