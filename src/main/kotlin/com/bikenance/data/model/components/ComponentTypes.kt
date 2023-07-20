package com.bikenance.data.model.components

enum class ComponentCategory(val order: Int) {
    TRANSMISSION(1), SUSPENSION(2), BRAKES(3), WHEELS(4), MISC(10)
}

sealed class ComponentType(val name: String, val category: ComponentCategory) {

    object Cassette : ComponentType(
        "CASSETTE", ComponentCategory.TRANSMISSION
    )

    object Chain : ComponentType(
        "CHAIN", ComponentCategory.TRANSMISSION
    )

    object RearDerailleurs : ComponentType(
        "REAR_DERAILLEURS", ComponentCategory.TRANSMISSION
    )

    object ChainRing : ComponentType(
        "CHAIN_RING", ComponentCategory.TRANSMISSION
    )

    object CableHousing : ComponentType(
        "CABLE_HOUSING", ComponentCategory.TRANSMISSION
    )

    object DiscBrake : ComponentType(
        "DISC_BRAKE", ComponentCategory.BRAKES
    )

    object DiscPad : ComponentType(
        "DISC_PAD", ComponentCategory.BRAKES
    )

    object BrakeLever : ComponentType(
        "BRAKE_LEVER", ComponentCategory.BRAKES
    )

    object Fork : ComponentType(
        "FORK", ComponentCategory.SUSPENSION
    )

    object RearSuspension : ComponentType(
        "REAR_SUSPENSION", ComponentCategory.SUSPENSION
    )

    object Wheel : ComponentType(
        "WHEEL", ComponentCategory.WHEELS
    )

    object FrontHub : ComponentType(
        "FRONT_HUB", ComponentCategory.WHEELS
    )

    object RearHub : ComponentType(
        "REAR_HUB", ComponentCategory.WHEELS
    )

    object Tire : ComponentType(
        "TIRE", ComponentCategory.WHEELS
    )

    object FrameBearings : ComponentType(
        "FRAME_BEARINGS", ComponentCategory.MISC
    )

    object DropperPost : ComponentType(
        "DROPPER_POST", ComponentCategory.MISC
    )

    object ThruAxle : ComponentType(
        "THRU_AXLE", ComponentCategory.MISC
    )

    object PedalClipless : ComponentType(
        "PEDAL_CLIPLESS", ComponentCategory.MISC
    )

    object HandlebarTape : ComponentType(
        "HANDLEBAR_TAPE", ComponentCategory.MISC
    )

    object Custom : ComponentType(
        "CUSTOM", ComponentCategory.MISC
    )

    companion object {

        private val allComponentTypes: List<ComponentType> by lazy {
            listOf(
                Cassette,
                Chain,
                RearDerailleurs,
                ChainRing,
                CableHousing,
                DiscBrake,
                DiscPad,
                BrakeLever,
                Fork,
                RearSuspension,
                Wheel,
                FrontHub,
                RearHub,
                Tire,
                FrameBearings,
                DropperPost,
                ThruAxle,
                PedalClipless,
                HandlebarTape,
                Custom
            )
        }

        fun getAll(): List<ComponentType> {
            return allComponentTypes
        }

        fun getByName(name: String): ComponentType {
            return allComponentTypes.find { it.name == name } ?: Custom
        }
    }
}