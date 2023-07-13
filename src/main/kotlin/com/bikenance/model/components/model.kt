package com.bikenance.model.components

import com.bikenance.database.mongodb.MongoModel
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.serialization.SerialName


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
    var usageSinceLastReplace: Usage = Usage(0.0, 0.0)
) : MongoModel<BikeComponent>()


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

enum class MaintenanceTypes {
    BRAKE_MAINTENANCE,
    CABLES_AND_HOUSING_MAINTENANCE,
    CASSETTE_MAINTENANCE,
    CHAIN_MAINTENANCE,
    DISC_BRAKE_MAINTENANCE,
    DROPPER_POST_MAINTENANCE,
    FORK_MAINTENANCE,
    FRONT_HUB_MAINTENANCE,
    REAR_SUSPENSION_MAINTENANCE,
    THRU_AXLE_MAINTENANCE,
    TIRE_MAINTENANCE,
    WHEELSET_TUBELESS_MAINTENANCE,
    WHEELSET_WHEELS_AND_SPOKES_MAINTENANCE,
    WHEELSET_TREAD_WEAR_MAINTENANCE
}

enum class ComponentModifier {
    REAR,
    FRONT
}

enum class RevisionUnit {
    KILOMETERS,
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
    @JsonProperty("hours")
    val hours: Double,
    @JsonProperty("km")
    val km: Double
)

data class MaintenanceInfo(
    @JsonProperty("type")
    val type: String,
    @JsonProperty("description")
    val description: String,
    @JsonProperty("longDescription")
    val longDescription: String,
    @JsonProperty("defaultFrequency")
    val defaultFrequency: RevisionFrequency,
    @JsonProperty("componentType")
    val componentType: ComponentTypes
) {
    fun maintenanceType(): MaintenanceTypes {
        return try {
            MaintenanceTypes.valueOf(type)
        } catch (e: Exception) {
            throw e
            // TODO: return ComponentTypes.CUSTOM
        }
    }
}

data class Maintenance(
    @JsonProperty("type")
    val type: MaintenanceInfo,
    @JsonProperty("usage")
    var usageSinceLastMaintenance: Usage = Usage(0.0, 0.0),
    @JsonProperty("dueDate")
    var dateTime: String? = null,
)


