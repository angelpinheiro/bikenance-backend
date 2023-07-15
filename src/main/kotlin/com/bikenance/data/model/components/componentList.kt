package com.bikenance.data.model.components


val defaultComponentTypes = mapOf(

    ComponentTypes.BRAKE_LEVER to ComponentInfo(
        ComponentTypes.BRAKE_LEVER.name,
        "Brake Lever",
        "Controls the bike's braking system."
    ),
    ComponentTypes.CASSETTE to ComponentInfo(
        ComponentTypes.CASSETTE.name,
        "Cassette",
        "Gears on the rear wheel hub."
    ),
    ComponentTypes.DISC_BRAKE to ComponentInfo(
        ComponentTypes.DISC_BRAKE.name,
        "Brake Disc",
        "Braking system using disc rotor and caliper."
    ),
    ComponentTypes.FORK to ComponentInfo(
        ComponentTypes.FORK.name,
        "Fork",
        "Front wheel holder with suspension and steering."
    ),
    ComponentTypes.PEDAL_CLIPLESS to ComponentInfo(
        ComponentTypes.PEDAL_CLIPLESS.name,
        "Clipless Pedal",
        "Securely attaches shoes to pedals."
    ),
    ComponentTypes.REAR_HUB to ComponentInfo(
        ComponentTypes.REAR_HUB.name,
        "Rear Hub",
        "Center of rear wheel with bearings."
    ),
    ComponentTypes.THRU_AXLE to ComponentInfo(
        ComponentTypes.THRU_AXLE.name,
        "Thru Axle",
        "Stiff and secure axle for wheels."
    ),
    ComponentTypes.WHEELSET to ComponentInfo(
        ComponentTypes.WHEELSET.name,
        "Wheelset",
        "Pair of wheels including rims, spokes, and hubs."
    ),
    ComponentTypes.CABLE_HOUSING to ComponentInfo(
        ComponentTypes.CABLE_HOUSING.name,
        "Cable Housing & Cables",
        "Shifting and braking cables and their protective covering."
    ),
    ComponentTypes.CHAIN to ComponentInfo(
        ComponentTypes.CHAIN.name,
        "Chain",
        "Transfers power from pedals to rear wheel."
    ),
    ComponentTypes.DISC_PAD to ComponentInfo(
        ComponentTypes.DISC_PAD.name,
        "Disc Pad",
        "Brake pads for disc brakes."
    ),
    ComponentTypes.DROPER_POST to ComponentInfo(
        ComponentTypes.DROPER_POST.name,
        "Dropper Post",
        "Adjustable seatpost for on-the-fly height changes."
    ),
    ComponentTypes.FRONT_HUB to ComponentInfo(
        ComponentTypes.FRONT_HUB.name,
        "Front Hub",
        "Center of front wheel with bearings."
    ),
    ComponentTypes.REAR_DERAUILLEURS to ComponentInfo(
        ComponentTypes.REAR_DERAUILLEURS.name,
        "Rear Derailleurs",
        "Shifts the chain between gears at the rear wheel."
    ),
    ComponentTypes.REAR_SUSPENSION to ComponentInfo(
        ComponentTypes.REAR_SUSPENSION.name,
        "Rear Suspension",
        "Provides suspension at the rear wheel."
    ),
    ComponentTypes.TIRE to ComponentInfo(
        ComponentTypes.TIRE.name,
        "Tire",
        "Rubber outer layer for the wheels."
    )
)