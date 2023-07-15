package com.bikenance.util

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue

val mapper: ObjectMapper = jacksonObjectMapper()
    .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)

/**
 * All classes implementing StravaModel will have a method for converting them to other VO
 * objects using jackson serialization and deserialization. This way we can easily keep the
 * model used by strava webhooks from our custom internal model
 */
open class StravaModel<T>() {
    inline fun <reified T : Any> reSerialize(): T {
        val str = mapper.writeValueAsString(this)
        return mapper.readValue<T>(str)
    }
}