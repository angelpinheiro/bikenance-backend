package com.bikenance.data.model.components
val maintenanceTypeList = listOf(
    MaintenanceInfo(
        type = MaintenanceTypes.BRAKE_MAINTENANCE.name,
        description = "Maintenance of Brakes",
        longDescription = "Hydraulic circuit purging and piston maintenance.",
        defaultFrequency = RevisionFrequency(5000, RevisionUnit.KILOMETERS),
        componentType = ComponentTypes.BRAKE_LEVER
    ),
    MaintenanceInfo(
        type = MaintenanceTypes.CABLES_AND_HOUSING_MAINTENANCE.name,
        description = "Maintenance of Cables and Housings",
        longDescription = "Visual inspection of cable housings for wear or damage. Replace if necessary.",
        defaultFrequency = RevisionFrequency(5000, RevisionUnit.KILOMETERS),
        componentType = ComponentTypes.CABLE_HOUSING
    ),
    MaintenanceInfo(
        type = MaintenanceTypes.CASSETTE_MAINTENANCE.name,
        description = "Maintenance of Cassette",
        longDescription = "Check the wear of the cassette. Consider replacing if it shows excessive wear or hinders gear shifting.",
        defaultFrequency = RevisionFrequency(2000, RevisionUnit.KILOMETERS),
        componentType = ComponentTypes.CASSETTE
    ),
    MaintenanceInfo(
        type = MaintenanceTypes.CHAIN_MAINTENANCE.name,
        description = "Maintenance of Chain",
        longDescription = "Replace the chain.",
        defaultFrequency = RevisionFrequency(2000, RevisionUnit.KILOMETERS),
        componentType = ComponentTypes.CHAIN
    ),
    MaintenanceInfo(
        type = MaintenanceTypes.DISC_BRAKE_MAINTENANCE.name,
        description = "Maintenance of Disc Brakes",
        longDescription = "Check the wear of the disc brakes. Consider replacing if they are worn or deformed.",
        defaultFrequency = RevisionFrequency(5000, RevisionUnit.KILOMETERS),
        componentType = ComponentTypes.DISC_BRAKE
    ),
    MaintenanceInfo(
        type = MaintenanceTypes.DROPPER_POST_MAINTENANCE.name,
        description = "Maintenance of Dropper Post",
        longDescription = "Check and lubricate the adjustable seatpost. Ensure proper functioning without play or lockout.",
        defaultFrequency = RevisionFrequency(5000, RevisionUnit.KILOMETERS),
        componentType = ComponentTypes.DROPER_POST
    ),
    MaintenanceInfo(
        type = MaintenanceTypes.FORK_MAINTENANCE.name,
        description = "Maintenance of Fork",
        longDescription = "Check and maintain the fork according to the manufacturer's recommendations.",
        defaultFrequency = RevisionFrequency(4000, RevisionUnit.KILOMETERS),
        componentType = ComponentTypes.FORK
    ),
    MaintenanceInfo(
        type = MaintenanceTypes.FRONT_HUB_MAINTENANCE.name,
        description = "Maintenance of Front Hub",
        longDescription = "Check the condition and lubrication of the front hub.",
        defaultFrequency = RevisionFrequency(5000, RevisionUnit.KILOMETERS),
        componentType = ComponentTypes.FRONT_HUB
    ),
    MaintenanceInfo(
        type = MaintenanceTypes.REAR_SUSPENSION_MAINTENANCE.name,
        description = "Maintenance of Rear Suspension",
        longDescription = "Check and maintain the rear suspension according to the manufacturer's recommendations.",
        defaultFrequency = RevisionFrequency(4000, RevisionUnit.KILOMETERS),
        componentType = ComponentTypes.REAR_SUSPENSION
    ),
    MaintenanceInfo(
        type = MaintenanceTypes.THRU_AXLE_MAINTENANCE.name,
        description = "Maintenance of Thru Axle",
        longDescription = "Check the condition and proper tightening of the thru axle.",
        defaultFrequency = RevisionFrequency(5000, RevisionUnit.KILOMETERS),
        componentType = ComponentTypes.THRU_AXLE
    ),
    MaintenanceInfo(
        type = MaintenanceTypes.TIRE_MAINTENANCE.name,
        description = "Maintenance of Tires",
        longDescription = "Check the tread wear of the tires. Consider replacing if they are worn.",
        defaultFrequency = RevisionFrequency(1000, RevisionUnit.KILOMETERS),
        componentType = ComponentTypes.TIRE
    ),
    MaintenanceInfo(
        type = MaintenanceTypes.WHEELSET_TUBELESS_MAINTENANCE.name,
        description = "Tubeless Fluid Replacement",
        longDescription = "Replace tubeless fluid every 1000 km.",
        defaultFrequency = RevisionFrequency(1000, RevisionUnit.KILOMETERS),
        componentType = ComponentTypes.WHEELSET
    ),
    MaintenanceInfo(
        type = MaintenanceTypes.WHEELSET_WHEELS_AND_SPOKES_MAINTENANCE.name,
        description = "Wheels and Spokes Inspection",
        longDescription = "Inspect the condition of wheels and spokes every 1000 km. Ensure they are properly tensioned and free from visible damage.",
        defaultFrequency = RevisionFrequency(1000, RevisionUnit.KILOMETERS),
        componentType = ComponentTypes.WHEELSET
    ),
    MaintenanceInfo(
        type = MaintenanceTypes.WHEELSET_TREAD_WEAR_MAINTENANCE.name,
        description = "Tread Wear Inspection",
        longDescription = "Inspect the tread wear of the tires every 4000 km. Consider replacing if they are worn.",
        defaultFrequency = RevisionFrequency(4000, RevisionUnit.KILOMETERS),
        componentType = ComponentTypes.WHEELSET
    )
)
