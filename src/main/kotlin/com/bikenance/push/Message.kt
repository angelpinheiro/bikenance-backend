package com.bikenance.push

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonValue


enum class MessageType(
    @JsonValue
    @JsonProperty("message_type") val type: String
) {
    PROFILE_SYNC("PROFILE_UPDATED"),
    NEW_ACTIVITY("NEW_ACTIVITY"),
    RIDES_UPDATED("RIDES_UPDATED"),
    RIDES_DELETED("RIDES_DELETED")
}

data class MessageData(
    val appMessageType: MessageType,
    val messageParams: Map<String, String> = mapOf()
)