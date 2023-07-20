package com.bikenance.data.model.components

/**
 * Default list of maintenances. In future versions, this will be stored on the user profile and will be
 * editable from the app, so users can adapt maintenance intervals according to their preferences
 */

//TODO: Add maintenance priority

val defaultMaintenances = listOf(
    MaintenanceInfo(
        type = MaintenanceType.BrakeMaintenance,
        defaultFrequency = RevisionFrequency(5000, RevisionUnit.KILOMETERS)
    ),
    MaintenanceInfo(
        type = MaintenanceType.DiscPadMaintenance,
        defaultFrequency = RevisionFrequency(1000, RevisionUnit.KILOMETERS)
    ),
    MaintenanceInfo(
        type = MaintenanceType.CablesAndHousingMaintenance,
        defaultFrequency = RevisionFrequency(6000, RevisionUnit.KILOMETERS)
    ),
    MaintenanceInfo(
        type = MaintenanceType.CassetteMaintenance,
        defaultFrequency = RevisionFrequency(3000, RevisionUnit.KILOMETERS)
    ),
    MaintenanceInfo(
        type = MaintenanceType.ChainRingMaintenance,
        defaultFrequency = RevisionFrequency(3000, RevisionUnit.KILOMETERS)
    ),
    MaintenanceInfo(
        type = MaintenanceType.RearDerailleurMaintenance,
        defaultFrequency = RevisionFrequency(3000, RevisionUnit.KILOMETERS)
    ),
    MaintenanceInfo(
        type = MaintenanceType.ChainMaintenance,
        defaultFrequency = RevisionFrequency(1200, RevisionUnit.KILOMETERS)
    ),
    MaintenanceInfo(
        type = MaintenanceType.DiscBrakeMaintenance,
        defaultFrequency = RevisionFrequency(6000, RevisionUnit.KILOMETERS)
    ),
    MaintenanceInfo(
        type = MaintenanceType.DropperPostMaintenance,
        defaultFrequency = RevisionFrequency(6000, RevisionUnit.KILOMETERS)
    ),
    MaintenanceInfo(
        type = MaintenanceType.ForkMaintenance,
        defaultFrequency = RevisionFrequency(4000, RevisionUnit.KILOMETERS)
    ),
    MaintenanceInfo(
        type = MaintenanceType.FrontHubMaintenance,
        defaultFrequency = RevisionFrequency(5000, RevisionUnit.KILOMETERS)
    ),
    MaintenanceInfo(
        type = MaintenanceType.RearHubMaintenance,
        defaultFrequency = RevisionFrequency(5000, RevisionUnit.KILOMETERS)
    ),
    MaintenanceInfo(
        type = MaintenanceType.RearSuspensionMaintenance,
        defaultFrequency = RevisionFrequency(4000, RevisionUnit.KILOMETERS)
    ),
    MaintenanceInfo(
        type = MaintenanceType.ThruAxleMaintenance,
        defaultFrequency = RevisionFrequency(3000, RevisionUnit.KILOMETERS)
    ),
    MaintenanceInfo(
        type = MaintenanceType.FrameBearingsMaintenance,
        defaultFrequency = RevisionFrequency(4000, RevisionUnit.KILOMETERS)
    ),
    MaintenanceInfo(
        type = MaintenanceType.TireMaintenance,
        defaultFrequency = RevisionFrequency(3000, RevisionUnit.KILOMETERS)
    ),
    MaintenanceInfo(
        type = MaintenanceType.WheelTubelessMaintenance,
        defaultFrequency = RevisionFrequency(1000, RevisionUnit.KILOMETERS)
    ),
    MaintenanceInfo(
        type = MaintenanceType.WheelSpokesMaintenance,
        defaultFrequency = RevisionFrequency(2000, RevisionUnit.KILOMETERS)
    ),
    MaintenanceInfo(
        type = MaintenanceType.CustomMaintenance,
        defaultFrequency = RevisionFrequency(0, RevisionUnit.KILOMETERS) // Cambiar por la frecuencia adecuada
    )
)
