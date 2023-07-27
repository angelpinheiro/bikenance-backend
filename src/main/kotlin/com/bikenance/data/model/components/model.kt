package com.bikenance.data.model.components

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime
import java.util.*

data class BikeComponent(
    @JsonProperty("_id")
    val _id: String = UUID.randomUUID().toString(),
    @JsonProperty("bikeId")
    val bikeId: String?,
    @JsonProperty("alias")
    val alias: String? = null,
    @JsonProperty("type")
    val type: ComponentType,
    @JsonProperty("maintenances")
    val maintenance: List<Maintenance>? = null,
    @JsonProperty("modifier")
    val modifier: ComponentModifier? = null,
    @JsonProperty("usage")
    var usage: Usage = Usage(0.0, 0.0),
    @JsonProperty("from")
    var from: LocalDateTime? = null
)

enum class ComponentModifier {
    REAR,
    FRONT
}

enum class RevisionUnit {
    KILOMETERS,
    HOURS,
    WEEKS,
    MONTHS
}

data class RevisionFrequency(
    @JsonProperty("every")
    val every: Int,
    @JsonProperty("unit")
    val unit: RevisionUnit
)

data class Usage(
    @JsonProperty("duration")
    val duration: Double = 0.0,
    @JsonProperty("distance")
    val distance: Double = 0.0,
    @JsonProperty("elevationGain")
    val elevationGain: Double = 0.0,
)

enum class MaintenancePriority {
    LOW, MEDIUM, HIGH
}

data class MaintenanceInfo(
    @JsonProperty("type")
    val type: MaintenanceType,
    @JsonProperty("defaultFrequency")
    val defaultFrequency: RevisionFrequency,
    @JsonProperty("priority")
    val priority: MaintenancePriority = MaintenancePriority.MEDIUM
)

data class Maintenance(
    @JsonProperty("_id")
    val _id: String = UUID.randomUUID().toString(),
    @JsonProperty("componentId")
    val componentId: String,
    @JsonProperty("type")
    val type: MaintenanceType,
    @JsonProperty("description")
    val description: String,
    @JsonProperty("defaultFrequency")
    val defaultFrequency: RevisionFrequency,
    @JsonProperty("componentType")
    val componentType: ComponentType,
    @JsonProperty("usageSinceLast")
    var usageSinceLast: Usage?,
    @JsonProperty("status")
    var status: Double = 0.0,
    @JsonProperty("lastDate")
    var lastMaintenanceDate: LocalDateTime? = null,
    @JsonProperty("estimatedDate")
    var estimatedDate: LocalDateTime? = null,
)



