package com.bikenance.strava.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonValue

enum class AspectType(
    @JsonValue
    @JsonProperty("aspect_type") val type: String
) {
    CREATE("create"),
    UPDATE("update"),
    DELETE("delete");
}

enum class ObjectType(
    @JsonValue
    @JsonProperty("object_type") val type: String
) {
    ATHLETE("athlete"),
    ACTIVITY("activity"),
}

data class EventData(
    @JsonProperty("aspect_type") var aspectType: AspectType,
    @JsonProperty("object_type") var objectType: ObjectType,
    @JsonProperty("event_time") var eventTime: String,
    @JsonProperty("object_id") var objectId: String,
    @JsonProperty("owner_id") var ownerId: String,
    @JsonProperty("subscription_id") var subscriptionId: String,
)

object StravaRequestParams {
    const val CLIENT_ID = "client_id"
    const val CLIENT_SECRET = "client_secret"
    const val CALLBACK_URL = "callback_url"
    const val VERIFY_TOKEN = "verify_token"
    const val GRANT_TYPE = "grant_type"
    const val REFRESH_TOKEN = "refresh_token"
}