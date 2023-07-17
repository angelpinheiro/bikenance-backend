package com.bikenance.usecase

import com.bikenance.data.database.BikeDao
import com.bikenance.data.database.BikeRideDao
import com.bikenance.data.model.Bike
import com.bikenance.data.model.components.Usage
import io.ktor.util.logging.*

class SetupBikeUseCase(
    private val bikeDao: BikeDao, private val rideDao: BikeRideDao
) {

    val log = KtorSimpleLogger("SetupBikeUseCase")

    suspend operator fun invoke(bikeId: String, bike: Bike): Bike {

        val updatedComponents = bike.components?.map { bikeComponent ->
            val lastMaintenance = bikeComponent.from

            val usage = lastMaintenance?.let {
                val rides = rideDao.getByBikeIdAfter(bikeId, it)
                val distance = rides.sumOf { r -> r.distance ?: 0 }
                val duration = rides.sumOf { r -> r.movingTime ?: 0 }
                log.debug("Usage: $distance, $duration")
                Usage(distance.toDouble(), duration.toDouble())
            } ?: Usage(0.0, 0.0)

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