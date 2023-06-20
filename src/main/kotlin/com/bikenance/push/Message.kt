package com.bikenance.push

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonValue


enum class MessageType(
    @JsonValue
    @JsonProperty("message_type") val type: String
) {
    NEW_ACTIVITY("NEW_ACTIVITY"),
    RIDES_UPDATED("RIDES_UPDATED"),
    RIDES_DELETED("RIDES_DELETED")
}
//
//data class Message(
//    @JsonProperty("data")
//    val data: MessageData,
//    @JsonProperty("registration_ids")
//    val registrationIds: List<String>
//)

data class MessageData(
    val appMessageType: MessageType,
    val messageParams: Map<String, String> = mapOf()
)