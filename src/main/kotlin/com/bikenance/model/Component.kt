package com.bikenance.model

import com.bikenance.database.mongodb.MongoModel
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.concurrent.TimeUnit

/**
 * Represents the types of components for a bike.
 */
data class ComponentType(
    val id: String,
    val name: String
)

/**
 * Represents a component of a bike.
 *
 * @property type The type of the component.
 * @property description A description of the component.
 * @property maintenance A list of maintenance tasks associated with the component.
 * @property usage The component's usage information.
 */
data class Component(
    val bikeId: String?,
    @JsonProperty("type")
    val type: ComponentType,
    @JsonProperty("description")
    val description: String? = null,
    @JsonProperty("maintenance")
    val maintenance: List<Maintenance>? = null,
    @JsonProperty("usage")
    var usage: Usage = Usage(0.0,0.0)
) : MongoModel<Component>()

/**
 * Represents a maintenance task for a component.
 *
 * @property description A description of the maintenance task.
 * @property frequency The frequency at which the maintenance task should be performed.
 */
data class Maintenance(
    @JsonProperty("description")
    val description: String,
    @JsonProperty("frequency")
    val frequency: RevisionFrequency
)

/**
 * Represents the frequency of a maintenance task.
 *
 * @property every The frequency interval for the maintenance task.
 * @property unit The time unit for the frequency interval.
 */
data class RevisionFrequency(
    @JsonProperty("every")
    val every: Int,
    @JsonProperty("unit")
    val unit: TimeUnit
)

/**
 * Represents the usage information of a component.
 *
 * @property hours The number of hours the component has been used.
 * @property km The distance in kilometers the component has been used.
 */
data class Usage(
    @JsonProperty("hours")
    val hours: Double,
    @JsonProperty("km")
    val km: Double
)


val componentTypes = mapOf(
    "FRAME" to ComponentType("FRAME", "Frame"),
    "FORK" to ComponentType("FORK", "Front suspension"),
    "HANDLEBAR" to ComponentType("HANDLEBAR", "Handlebar"),
    "BRAKES" to ComponentType("BRAKES", "Brakes"),
    "DERAILLEURS" to ComponentType("DERAILLEURS", "Derailleurs"),
    "CHAIN" to ComponentType("CHAIN", "Chain"),
    "PEDALS" to ComponentType("PEDALS", "Pedals"),
    "RIMS" to ComponentType("RIMS", "Rims"),
    "TIRES" to ComponentType("TIRES", "Tires"),
    "SADDLE" to ComponentType("SADDLE", "Saddle"),
    "CABLES" to ComponentType("CABLES", "Cables and housings"),
    "BOTTOM_BRACKET" to ComponentType("BOTTOM_BRACKET", "Bottom bracket"),
    "HEADSET" to ComponentType("HEADSET", "Headset")
)



