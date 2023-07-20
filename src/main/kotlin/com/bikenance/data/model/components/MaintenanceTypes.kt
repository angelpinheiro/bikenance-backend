package com.bikenance.data.model.components

sealed class MaintenanceType(val name: String, val componentType: ComponentType) {

    object BrakeMaintenance : MaintenanceType(
        "BRAKE_MAINTENANCE", ComponentType.BrakeLever
    )

    object DiscPadMaintenance : MaintenanceType(
        "DISC_PAD_MAINTENANCE", ComponentType.DiscPad
    )

    object CablesAndHousingMaintenance : MaintenanceType(
        "CABLES_AND_HOUSING_MAINTENANCE", ComponentType.CableHousing
    )

    object CassetteMaintenance : MaintenanceType(
        "CASSETTE_MAINTENANCE", ComponentType.Cassette
    )

    object RearDerailleurMaintenance : MaintenanceType(
        "REAR_DERAILLEUR_MAINTENANCE", ComponentType.RearDerailleurs
    )

    object ChainMaintenance : MaintenanceType(
        "CHAIN_MAINTENANCE", ComponentType.Chain
    )

    object ChainRingMaintenance : MaintenanceType(
        "CHAIN_RING_MAINTENANCE", ComponentType.ChainRing
    )

    object DiscBrakeMaintenance : MaintenanceType(
        "DISC_BRAKE_MAINTENANCE", ComponentType.DiscBrake
    )

    object DropperPostMaintenance : MaintenanceType(
        "DROPPER_POST_MAINTENANCE", ComponentType.DropperPost
    )

    object ForkMaintenance : MaintenanceType(
        "FORK_MAINTENANCE", ComponentType.Fork
    )

    object FrontHubMaintenance : MaintenanceType(
        "FRONT_HUB_MAINTENANCE", ComponentType.FrontHub
    )

    object RearHubMaintenance : MaintenanceType(
        "REAR_HUB_MAINTENANCE", ComponentType.RearHub
    )

    object RearSuspensionMaintenance : MaintenanceType(
        "REAR_SUSPENSION_MAINTENANCE", ComponentType.RearSuspension
    )

    object ThruAxleMaintenance : MaintenanceType(
        "THRU_AXLE_MAINTENANCE", ComponentType.ThruAxle
    )

    object FrameBearingsMaintenance : MaintenanceType(
        "FRAME_BEARINGS_MAINTENANCE", ComponentType.FrameBearings
    )

    object TireMaintenance : MaintenanceType(
        "TIRE_MAINTENANCE", ComponentType.Tire
    )

    object WheelTubelessMaintenance : MaintenanceType(
        "TUBELESS_MAINTENANCE", ComponentType.Wheel
    )

    object WheelSpokesMaintenance : MaintenanceType(
        "SPOKES_MAINTENANCE", ComponentType.Wheel
    )

    object CustomMaintenance : MaintenanceType(
        "CustomMaintenance", ComponentType.Custom
    )

    companion object {
        private val allMaintenanceTypes: List<MaintenanceType> by lazy {
            listOf(
                BrakeMaintenance,
                DiscPadMaintenance,
                CablesAndHousingMaintenance,
                CassetteMaintenance,
                RearDerailleurMaintenance,
                ChainMaintenance,
                ChainRingMaintenance,
                DiscBrakeMaintenance,
                DropperPostMaintenance,
                ForkMaintenance,
                FrontHubMaintenance,
                RearHubMaintenance,
                RearSuspensionMaintenance,
                ThruAxleMaintenance,
                FrameBearingsMaintenance,
                TireMaintenance,
                WheelTubelessMaintenance,
                WheelSpokesMaintenance,
                CustomMaintenance
            )
        }

        fun getAll(): List<MaintenanceType> {
            return allMaintenanceTypes
        }

        fun getByName(name: String): MaintenanceType {
            return allMaintenanceTypes.find { it.name == name } ?: CustomMaintenance
        }
    }
}