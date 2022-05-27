package com.bikenance.features.strava.usecase

import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.coroutines.*


data class EventData(
    @JsonProperty("aspect_type") var aspectType: String? = null,
    @JsonProperty("event_time") var eventTime: String? = null,
    @JsonProperty("object_id") var objectId: String? = null,
    @JsonProperty("object_type") var objectType: String? = null,
    @JsonProperty("owner_id") var ownerId: String? = null,
    @JsonProperty("subscription_id") var subscriptionId: String? = null,
) {
    companion object {
        const val TYPE_ATHLETE = "athlete"
        const val TYPE_ACTIVITY = "activity"
        const val ASPECT_CREATE = "create"
        const val ASPECT_UPDATE = "update"
    }
}


class ReceiveDataUseCase {


    suspend fun handleEventData(eventData: EventData) {
        println("Received event data")
        when (eventData.objectType) {
            EventData.TYPE_ATHLETE -> {

            }
            EventData.TYPE_ACTIVITY -> {

            }
        }
    }

}

