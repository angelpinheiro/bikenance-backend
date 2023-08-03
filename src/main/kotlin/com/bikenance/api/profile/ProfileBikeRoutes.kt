package com.bikenance.api.profile

import com.bikenance.api.apiResult
import com.bikenance.api.authApiResult
import com.bikenance.data.database.mongodb.DAOS
import com.bikenance.data.model.Bike
import com.bikenance.data.model.BikeUpdate
import com.bikenance.data.model.SyncBikesData
import com.bikenance.data.model.components.BikeComponent
import com.bikenance.data.model.components.Maintenance
import com.bikenance.usecase.SetupBikeUseCase
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.routing.Route
import io.ktor.util.logging.*
import org.koin.ktor.ext.inject

fun Route.profileBikeRoutes() {

    val log = KtorSimpleLogger("ProfileRoutes")

    val dao: DAOS by inject()
//    val stravaBikeSync: StravaBikeSync by inject()


    get<ProfilePath.Bikes> {
        authApiResult { userId ->
            dao.bikeDao.getByUserId(userId)
        }
    }

    post<ProfilePath.Bikes> { r ->
        val bike = call.receive<Bike>()
        authApiResult { userId ->
            bike.userId = userId
            dao.bikeDao.create(bike)
        }
    }

    put<ProfilePath.SyncBikes> {
        val syncBikes = call.receive<SyncBikesData>()
        authApiResult { userId ->
            val bikes = dao.bikeDao.getByUserId(userId)
            bikes.map { bike ->
                val sync = syncBikes.syncData[bike.oid()]
                sync?.let {
                    dao.bikeDao.updateSyncStatus(bike.oid(), it)
                }
            }
            true
        }
    }

    get<ProfilePath.Bikes.BikeById> { r ->
        apiResult {
            dao.bikeDao.getById(r.bikeId)
        }
    }

    get<ProfilePath.Bikes.BikeById.Components> { r ->
        apiResult {
            dao.bikeDao.getById(r.parent.bikeId)?.components ?: listOf()
        }
    }

    put<ProfilePath.Bikes.BikeById.Maintenance> { r ->
        apiResult {

            val bikeId = r.parent.bikeId
            val maintenanceId = r.maintenanceId
            val maintenance = call.receive<Maintenance>()

            log.debug("BikeById.Maintenance: $bikeId, $maintenanceId, areEqual: ${maintenanceId == maintenance._id}")

            if (maintenanceId != maintenance._id) {
                null
            } else {
                val r = dao.bikeDao.getById(bikeId)?.let { bike ->
                    bike.components?.firstOrNull { it.maintenance?.any { m -> m._id == maintenanceId } ?: false }
                        ?.let { targetComp ->
                            targetComp.maintenance?.find { it._id == maintenanceId }?.let { targetMaintenance ->

                                val newComponent = targetComp.copy(
                                    maintenance = targetComp.maintenance.minus(targetMaintenance).plus(maintenance)
                                )
                                val bikeUpdate =
                                    bike.copy(components = bike.components.minus(targetComp).plus(newComponent))
                                dao.bikeDao.update(bikeId, bikeUpdate)
                            }
                        }
                }

                if (r == true) {
                    dao.bikeDao.getById(bikeId)
                } else {
                    null
                }
            }
        }
    }

    post<ProfilePath.Bikes.BikeById.Components> { r ->

        val bikeId = r.parent.bikeId
        val components = call.receive<List<BikeComponent>>()

        apiResult {
            dao.bikeDao.getById(bikeId)?.let { bike ->
                dao.bikeDao.update(bikeId, bike.copy(components = components))
                dao.bikeDao.getById(bikeId)?.components ?: listOf()
            }
        }
    }


    put<ProfilePath.Bikes.BikeById> { r ->
        val bike = call.receive<BikeUpdate>()
        apiResult {
            dao.bikeDao.partialUpdate(r.bikeId, bike)
            dao.bikeDao.getById(r.bikeId)
        }
    }

    delete<ProfilePath.Bikes.BikeById> { r ->
        authApiResult { userId ->
            val user = dao.userDao.getById(userId) ?: throw Exception("User not found")
            dao.bikeDao.getById(r.bikeId)?.let { bike ->
                if (bike.stravaId != null) {
                    bike.draft = true
                    dao.bikeDao.update(bike.oid(), bike)
                    // stravaBikeSync.onBikeRemoved(user, bike)
                }
            }
        }
    }


    put<ProfilePath.Bikes.BikeById.Setup> { r ->
        apiResult {
            val bikeId = r.parent.bikeId
            val bike = call.receive<Bike>()
            SetupBikeUseCase(dao.bikeDao, dao.bikeRideDao).invoke(bikeId, bike.copy(
                components = bike.components?.map { it.ensureId() }
            ))
        }
    }


}