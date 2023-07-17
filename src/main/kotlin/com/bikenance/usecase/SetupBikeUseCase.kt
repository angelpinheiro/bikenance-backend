package com.bikenance.usecase

import com.bikenance.data.database.BikeDao
import com.bikenance.data.database.BikeRideDao
import com.bikenance.data.model.Bike
import com.bikenance.data.model.components.Usage
import com.bikenance.util.bknLogger
import com.bikenance.util.formatAsIsoDate
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
                log.info("Component ${bikeComponent.type} has an usage of [$distance  / $duration] ")
                Usage(duration = duration.toDouble(), distance = distance.toDouble())
            } else {
                Usage(0.0, 0.0)
            }

            bikeComponent.copy(
                usage = usage
            )
        }

        val updatedBike = bike.copy(
            configDone = true, components = updatedComponents
        )

        bikeDao.update(bikeId, updatedBike)
        return bikeDao.getById(bikeId) ?: throw Exception("Bike not found")
    }

}