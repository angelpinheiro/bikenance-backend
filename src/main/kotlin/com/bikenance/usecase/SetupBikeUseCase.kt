package com.bikenance.usecase

import com.bikenance.data.database.BikeDao
import com.bikenance.data.database.BikeRideDao
import com.bikenance.data.model.Bike
import com.bikenance.data.model.BikeStats
import com.bikenance.data.model.components.BikeComponent
import com.bikenance.data.model.components.Maintenance
import com.bikenance.data.model.components.Usage
import com.bikenance.data.model.components.defaultMaintenances
import com.bikenance.util.bknLogger
import com.bikenance.util.formatAsIsoDate
import java.time.LocalDateTime
import java.time.ZoneOffset

class SetupBikeUseCase(
    private val bikeDao: BikeDao, private val rideDao: BikeRideDao
) {

    val log = bknLogger("SetupBikeUseCase")

    suspend operator fun invoke(bikeId: String, bike: Bike): Bike {

        // get the farthest maintenance date
        val farthestMaintenance =
            bike.components?.filter { it.from != null }?.minByOrNull { it.from!!.toEpochSecond(ZoneOffset.UTC) }

        // get all rides after that date
        val rides = farthestMaintenance?.let { m ->
            rideDao.getByBikeIdAfter(bikeId, m.from!!)
        } ?: emptyList()

        log.info("Computing usage for {${bike.components?.size}} bike components")
        log.info("Farthest maintenance date: {${farthestMaintenance?.from?.formatAsIsoDate()}} ")
        log.info("Rides after farthest date: ${rides.size}")

        // For each component, compute usage based on the rides after the component "from" date
        val updatedComponents = bike.components?.map { bikeComponent ->

            // get rides after bikeComponent.from with positive distance (avoid virtual rides)
            val ridesFiltered = rides.filter { it.dateTime.isAfter(bikeComponent.from) && (it.distance ?: 0) > 0L }

            log.info("Rides after date: ${ridesFiltered.size}")

            val usage = if (bikeComponent.from != null) {
                val distance = ridesFiltered.sumOf { r -> r.distance ?: 0 }
                val duration = ridesFiltered.sumOf { r -> r.movingTime ?: 0 }
                val elevationGain = ridesFiltered.sumOf { r -> r.totalElevationGain ?: 0 }
                log.info("Component ${bikeComponent.type} has an usage of [$distance  / $duration] ")
                Usage(
                    duration = duration.toDouble(),
                    distance = distance.toDouble(),
                    elevationGain = elevationGain.toDouble()
                )
            } else {
                Usage(0.0, 0.0)
            }

            bikeComponent.copy(
                usage = usage, maintenance = maintenances(
                    bikeComponent.copy(
                        usage = usage
                    )
                )
            )
        }

        val bikeStats = computeBikeStats(bikeId)

        val updatedBike = bike.copy(
            configDone = true, components = updatedComponents, stats = bikeStats
        )

        bikeDao.update(bikeId, updatedBike)
        return bikeDao.getById(bikeId) ?: throw Exception("Bike not found")
    }

    private suspend fun computeBikeStats(bikeId: String): BikeStats {

        var rideCount = 0.0
        var duration = 0
        var distance = 0L
        var elevationGain = 0.0
        var averageSpeed = 0.0
        var maxSpeed = 0.0
        var lastRideDate: LocalDateTime? = null


        rideDao.getByBikeId(bikeId).filter { (it.distance ?: 0) > 0L }.map { b ->
            rideCount = rideCount.inc()
            duration += b.elapsedTime ?: 0
            distance += b.distance ?: 0
            elevationGain += b.totalElevationGain ?: 0
            averageSpeed += b.averageSpeed ?: 0.0

            if ((b.maxSpeed?.compareTo(maxSpeed) ?: -1) > 0) {
                maxSpeed = b.maxSpeed ?: 0.0
            }

            if (lastRideDate == null) {
                lastRideDate = b.dateTime
            }
        }

        if (rideCount > 0) {
            averageSpeed /= rideCount
        }

        return BikeStats(
            rideCount, duration.toDouble(), distance.toDouble(), elevationGain, averageSpeed, maxSpeed, lastRideDate
        )
    }

    private fun maintenances(bikeComponent: BikeComponent): List<Maintenance> {

        val usage = bikeComponent.usage

        return defaultMaintenances.filter { it.type.componentType == bikeComponent.type }.map { info ->

            Maintenance(
                type = info.type,
                componentId = bikeComponent._id,
                description = "",
                componentType = info.type.componentType,
                defaultFrequency = info.defaultFrequency,
                usageSinceLast = usage,
                lastMaintenanceDate = bikeComponent.from,
            )
        }
    }


}