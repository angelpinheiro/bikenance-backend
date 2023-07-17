package com.bikenance.api.profile

import com.bikenance.api.apiResult
import com.bikenance.api.authUserId
import com.bikenance.data.database.mongodb.DAOS
import com.bikenance.data.model.Bike
import com.bikenance.data.model.SyncBikesData
import com.bikenance.data.model.components.BikeComponent
import com.bikenance.usecase.SetupBikeUseCase
import com.bikenance.usecase.strava.StravaBikeSync
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.routing.Route
import io.ktor.util.logging.*
import org.koin.ktor.ext.inject

fun Route.profileBikeRoutes() {

    val log = KtorSimpleLogger("ProfileRoutes")

    val dao: DAOS by inject()
    val stravaBikeSync: StravaBikeSync by inject()


    get<ProfilePath.Bikes> { r ->
        apiResult {
            val authUserId = authUserId()
            authUserId?.let { userId ->
                dao.bikeDao.getByUserId(userId)
            }
        }
    }

    post<ProfilePath.Bikes> { r ->

        val bike = call.receive<Bike>()
        val authUserId = authUserId()
        apiResult {
            authUserId?.let { userId ->
                bike.userId = userId
                dao.bikeDao.create(bike)
            }
        }

    }

    put<ProfilePath.SyncBikes> {
        val syncBikes = call.receive<SyncBikesData>()

        apiResult {
            val authUserId = authUserId()
            authUserId?.let { userId ->
                val user = dao.userDao.getById(authUserId) ?: throw Exception("User not found")
                val bikes = dao.bikeDao.getByUserId(userId)

                bikes.map { bike ->
                    val sync = syncBikes.syncData[bike.oid()]
                    sync?.let {
                        dao.bikeDao.updateSyncStatus(bike.oid(), it)
                    }
                }
                true
            } ?: false
        }
    }

    get<ProfilePath.Bikes.BikeById> { r ->
        apiResult {
            dao.bikeDao.getById(r.bikeId)
        }
    }

    get<ProfilePath.Bikes.BikeById.Components> { r ->
        apiResult {
            dao.componentDao.getByBikeId(r.parent.bikeId)
        }
    }

    post<ProfilePath.Bikes.BikeById.Components> { r ->

        val bikeId = r.parent.bikeId
        val components = call.receive<List<BikeComponent>>()

        apiResult {
            components.mapNotNull { component ->
                dao.componentDao.create(component.copy(bikeId = bikeId))
            }
        }
    }


    put<ProfilePath.Bikes.BikeById> { r ->
        val bike = call.receive<Bike>()
        apiResult {
            dao.bikeDao.update(r.bikeId, bike)
            dao.bikeDao.getById(r.bikeId)
        }
    }

    delete<ProfilePath.Bikes.BikeById> { r ->
        apiResult {

            val authUserId = authUserId()
            authUserId?.let { userId ->

                val user = dao.userDao.getById(userId) ?: throw Exception("User not found")

                dao.bikeDao.getById(r.bikeId)?.let { bike ->
                    if (bike.stravaId != null) {
                        bike.draft = true
                        dao.bikeDao.update(bike.oid(), bike)
                        stravaBikeSync.onBikeRemoved(user, bike)
                    }
                }
            }
        }
    }

    put<ProfilePath.Bikes.BikeById.Setup> { r ->
        apiResult {
            val bikeId = r.parent.bikeId
            val bike = call.receive<Bike>()
            log.debug("Setup bike $bikeId")
            SetupBikeUseCase(dao.bikeDao, dao.bikeRideDao).invoke(bikeId, bike)
        }
    }


}