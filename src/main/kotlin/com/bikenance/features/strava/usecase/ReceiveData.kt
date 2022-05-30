package com.bikenance.features.strava.usecase

import com.bikenance.database.UserDao
import com.bikenance.database.mongodb.DB
import com.bikenance.features.strava.api.Strava
import com.bikenance.features.strava.api.StravaApi
import com.bikenance.repository.UserRepository
import com.fasterxml.jackson.annotation.JsonProperty


data class EventData(
    @JsonProperty("aspect_type") var aspectType: String,
    @JsonProperty("event_time") var eventTime: String,
    @JsonProperty("object_id") var objectId: String,
    @JsonProperty("object_type") var objectType: String,
    @JsonProperty("owner_id") var ownerId: String,
    @JsonProperty("subscription_id") var subscriptionId: String,
) {
    companion object {
        const val TYPE_ATHLETE = "athlete"
        const val TYPE_ACTIVITY = "activity"
        const val ASPECT_CREATE = "create"
        const val ASPECT_UPDATE = "update"
    }
}


class ReceiveDataUseCase() {

    suspend fun handleEventData(db: DB, strava: Strava, eventData: EventData) {

        val userRepository = UserRepository(UserDao())
        val user = userRepository.findByAthleteId(eventData.ownerId)

        println("Received event data ${eventData.ownerId}, ${user?.athleteToken}")
        when (eventData.objectType) {
            EventData.TYPE_ATHLETE -> {

            }
            EventData.TYPE_ACTIVITY -> {
                user?.athleteToken?.let {
                    val activity = strava.withToken(it).activity(eventData.objectId)
                    db.activities.insertOne(activity)
                }
            }
        }
    }

}

