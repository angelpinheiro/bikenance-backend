package com.bikenance.data.model.components

import com.bikenance.util.expectedNextMaintenanceDate
import com.bikenance.util.wearPercentage
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime
import java.util.*

data class BikeComponent(
    @JsonProperty("_id")
    val _id: String = newId(),
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
    val usage: Usage = Usage(0.0, 0.0),
    @JsonProperty("from")
    var from: LocalDateTime? = null
) {
    companion object {
        fun newId() = UUID.randomUUID().toString()
    }

    fun ensureId(): BikeComponent {
        return copy(_id = _id.ifEmpty { newId() })
    }

    fun withNewId(): BikeComponent {
        return copy(_id = newId())
    }
}

enum class ComponentModifier {
    REAR,
    FRONT
}

enum class RevisionUnit {
    KILOMETERS,
    HOURS,
    WEEKS,
    MONTHS,
    YEARS
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
) {
    fun plus(addMovingTime: Number, addDistance: Number, addElevationGain: Number): Usage {
        return copy(
            duration = this.duration + addMovingTime.toDouble(),
            distance = this.distance + addDistance.toDouble(),
            elevationGain = this.elevationGain + addElevationGain.toDouble()
        )
    }
}

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
    var usageSinceLast: Usage = Usage(),
    @JsonProperty("lastDate")
    val lastMaintenanceDate: LocalDateTime? = null,
) {
    val status: Double by lazy {
        wearPercentage(LocalDateTime.now())
    }

    val estimatedDate: LocalDateTime? by lazy {
        expectedNextMaintenanceDate(LocalDateTime.now())
    }

    companion object {
        fun newId() = UUID.randomUUID().toString()
    }

    fun ensureId(): Maintenance {
        return copy(_id = _id.ifEmpty { newId() })
    }

    fun withNewId(): Maintenance {
        return copy(_id = newId())
    }
}



