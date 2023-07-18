package com.bikenance.data.model.components

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime
import java.util.UUID


data class ComponentInfo(
    @JsonProperty("type")
    val type: String,
    @JsonProperty("name")
    val name: String,
    @JsonProperty("description")
    val description: String = ""
) {
    fun componentType(): ComponentTypes {
        return try {
            ComponentTypes.valueOf(type)
        } catch (e: Exception) {
            throw e
            // TODO: return ComponentTypes.CUSTOM
        }

    }
}

data class BikeComponent(
    @JsonProperty("_id")
    val _id: String = UUID.randomUUID().toString(),
    @JsonProperty("bikeId")
    val bikeId: String?,
    @JsonProperty("alias")
    val alias: String? = null,
    @JsonProperty("type")
    val type: ComponentTypes,
    @JsonProperty("maintenances")
    val maintenance: List<Maintenance>? = null,
    @JsonProperty("modifier")
    val modifier: ComponentModifier? = null,
    @JsonProperty("usage")
    var usage: Usage = Usage(0.0, 0.0),
    @JsonProperty("from")
    var from: LocalDateTime? = null
)


enum class ComponentTypes {
    BRAKE_LEVER,
    CABLE_HOUSING,
    CASSETTE,
    CHAIN,
    DISC_BRAKE,
    DISC_PAD,
    DROPER_POST,
    FORK,
    FRONT_HUB,
    PEDAL_CLIPLESS,
    REAR_DERAUILLEURS,
    REAR_HUB,
    REAR_SUSPENSION,
    THRU_AXLE,
    TIRE,
    WHEELSET,
    FRAME_BEARINGS,
    HANDLEBAR_TAPE,
    CUSTOM,
    UNKNOWN
}

enum class MaintenanceTypes(val componentType: ComponentTypes) {
    BRAKE_MAINTENANCE(ComponentTypes.BRAKE_LEVER),
    DISC_PAD_MAINTENANCE(ComponentTypes.DISC_PAD),
    CABLES_AND_HOUSING_MAINTENANCE(ComponentTypes.CABLE_HOUSING),
    CASSETTE_MAINTENANCE(ComponentTypes.CASSETTE),
    REAR_DERAILLEUR_MAINTENANCE(ComponentTypes.REAR_DERAUILLEURS),
    CHAIN_MAINTENANCE(ComponentTypes.CHAIN),
    DISC_BRAKE_MAINTENANCE(ComponentTypes.DISC_BRAKE),
    DROPPER_POST_MAINTENANCE(ComponentTypes.DROPER_POST),
    FORK_MAINTENANCE(ComponentTypes.FORK),
    FRONT_HUB_MAINTENANCE(ComponentTypes.FRONT_HUB),
    REAR_HUB_MAINTENANCE(ComponentTypes.REAR_HUB),
    REAR_SUSPENSION_MAINTENANCE(ComponentTypes.REAR_SUSPENSION),
    THRU_AXLE_MAINTENANCE(ComponentTypes.THRU_AXLE),
    FRAME_BEARINGS_MAINTENANCE(ComponentTypes.FRAME_BEARINGS),
    TIRE_MAINTENANCE(ComponentTypes.TIRE),
    WHEELSET_TUBELESS_MAINTENANCE(ComponentTypes.WHEELSET),
    WHEELSET_WHEELS_AND_SPOKES_MAINTENANCE(ComponentTypes.WHEELSET),
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
)

data class MaintenanceInfo(
    @JsonProperty("type")
    val type: MaintenanceTypes,
    @JsonProperty("defaultFrequency")
    val defaultFrequency: RevisionFrequency
)

data class Maintenance(
    @JsonProperty("_id")
    val _id: String = UUID.randomUUID().toString(),
    @JsonProperty("componentId")
    val componentId: String,
    @JsonProperty("type")
    val type: MaintenanceTypes,
    @JsonProperty("description")
    val description: String,
    @JsonProperty("defaultFrequency")
    val defaultFrequency: RevisionFrequency,
    @JsonProperty("componentType")
    val componentType: ComponentTypes,
    @JsonProperty("usageSinceLast")
    var usageSinceLast: Usage?,
    @JsonProperty("status")
    var status: Double = 0.0,
    @JsonProperty("lastDate")
    var lastMaintenanceDate: LocalDateTime? = null,
    @JsonProperty("estimatedDate")
    var estimatedDate: LocalDateTime? = null,
)



