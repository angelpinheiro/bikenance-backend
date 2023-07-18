package com.bikenance.data.model.components

/**
 * Default list of maintenances. In future versions, this will be stored on the user profile and will be
 * editable from the app, so users can adapt maintenance intervals according to their preferences
 */

//TODO: Add maintenance priority

val defaultMaintenances = listOf(
    MaintenanceInfo(
        type = MaintenanceTypes.BRAKE_MAINTENANCE,
        defaultFrequency = RevisionFrequency(5000, RevisionUnit.KILOMETERS),
    ),
    MaintenanceInfo(
        type = MaintenanceTypes.DISC_PAD_MAINTENANCE,
        defaultFrequency = RevisionFrequency(1000, RevisionUnit.KILOMETERS)
    ),
    MaintenanceInfo(
        type = MaintenanceTypes.CABLES_AND_HOUSING_MAINTENANCE,
        defaultFrequency = RevisionFrequency(6000, RevisionUnit.KILOMETERS),
    ),
    MaintenanceInfo(
        type = MaintenanceTypes.CASSETTE_MAINTENANCE,
        defaultFrequency = RevisionFrequency(3000, RevisionUnit.KILOMETERS),
    ),
    MaintenanceInfo(
        type = MaintenanceTypes.REAR_DERAILLEUR_MAINTENANCE,
        defaultFrequency = RevisionFrequency(3000, RevisionUnit.KILOMETERS),
    ),
    MaintenanceInfo(
        type = MaintenanceTypes.CHAIN_MAINTENANCE,
        defaultFrequency = RevisionFrequency(1200, RevisionUnit.KILOMETERS),
    ),
    MaintenanceInfo(
        type = MaintenanceTypes.DISC_BRAKE_MAINTENANCE,
        defaultFrequency = RevisionFrequency(6000, RevisionUnit.KILOMETERS),
    ),
    MaintenanceInfo(
        type = MaintenanceTypes.DROPPER_POST_MAINTENANCE,
        defaultFrequency = RevisionFrequency(6000, RevisionUnit.KILOMETERS),
    ),
    MaintenanceInfo(
        type = MaintenanceTypes.FORK_MAINTENANCE,
        defaultFrequency = RevisionFrequency(4000, RevisionUnit.KILOMETERS),
    ),
    MaintenanceInfo(
        type = MaintenanceTypes.FRONT_HUB_MAINTENANCE,
        defaultFrequency = RevisionFrequency(5000, RevisionUnit.KILOMETERS),
    ),
    MaintenanceInfo(
        type = MaintenanceTypes.REAR_HUB_MAINTENANCE,
        defaultFrequency = RevisionFrequency(5000, RevisionUnit.KILOMETERS),
    ),
    MaintenanceInfo(
        type = MaintenanceTypes.REAR_SUSPENSION_MAINTENANCE,
        defaultFrequency = RevisionFrequency(4000, RevisionUnit.KILOMETERS),
    ),
    MaintenanceInfo(
        type = MaintenanceTypes.FRAME_BEARINGS_MAINTENANCE,
        defaultFrequency = RevisionFrequency(4000, RevisionUnit.KILOMETERS),
    ),
    MaintenanceInfo(
        type = MaintenanceTypes.THRU_AXLE_MAINTENANCE,
        defaultFrequency = RevisionFrequency(3000, RevisionUnit.KILOMETERS),
    ),
    MaintenanceInfo(
        type = MaintenanceTypes.TIRE_MAINTENANCE,
        defaultFrequency = RevisionFrequency(3000, RevisionUnit.KILOMETERS),
    ),
    MaintenanceInfo(
        type = MaintenanceTypes.WHEELSET_TUBELESS_MAINTENANCE,
        defaultFrequency = RevisionFrequency(1000, RevisionUnit.KILOMETERS),
    ),
    MaintenanceInfo(
        type = MaintenanceTypes.WHEELSET_WHEELS_AND_SPOKES_MAINTENANCE,
        defaultFrequency = RevisionFrequency(2000, RevisionUnit.KILOMETERS),
    )
)
