package com.bikenance.data.network.routing

import com.bikenance.data.database.mongodb.DAOS
import com.bikenance.data.database.mongodb.DB
import com.bikenance.data.model.*
import com.bikenance.data.model.components.BikeComponent
import com.bikenance.data.model.strava.StravaActivity
import com.bikenance.data.network.stravaApi.Strava
import com.bikenance.data.network.stravaApi.supportedActivityTypes
import com.bikenance.data.repository.UserRepository
import com.bikenance.usecase.SetupBikeUseCase
import com.bikenance.usecase.strava.StravaBikeSync
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.resources.put
import io.ktor.server.routing.*
import io.ktor.util.logging.*
import kotlinx.serialization.Serializable
import org.koin.ktor.ext.inject
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import java.time.LocalDateTime


@Serializable
@Resource("/profile")
class ProfilePath() {

    @Serializable
    @Resource("/extended")
    class Extended(val parent: ProfilePath = ProfilePath(), val draft: Boolean = false)

    @Serializable
    @Resource("/setup")
    class Setup(val parent: ProfilePath = ProfilePath())


    @Serializable
    @Resource("/bikes")
    class Bikes(val parent: ProfilePath = ProfilePath(), val draft: Boolean = false) {

        @Serializable
        @Resource("{bikeId}")
        class BikeById(val parent: Bikes = Bikes(), val bikeId: String) {
            @Serializable
            @Resource("components")
            class Components(val parent: BikeById)

            @Serializable
            @Resource("setup")
            class Setup(val parent: BikeById)
        }
    }

    @Serializable
    @Resource("/rides")
    class Rides(val parent: ProfilePath = ProfilePath(), val page: Int = 0, val pageSize: Int = 10)

    @Serializable
    @Resource("/pagedRides")
    class PagedByKeyRides(val parent: ProfilePath = ProfilePath(), val pageSize: Int = 10, val key: String? = null)

    @Serializable
    @Resource("/sync")
    class SyncBikes(val parent: ProfilePath = ProfilePath())

}


suspend fun getUserProfile(dao: DAOS, userId: String, includeDraftBikes: Boolean): ExtendedProfile {
    val profile = dao.profileDao.getByUserId(userId)
    val bikes = dao.bikeDao.getByUserId(userId)
    val bikeRides = dao.bikeRideDao.getByUserId(userId)

    return ExtendedProfile(
        profile,
        if (includeDraftBikes) bikes else bikes.filter { !it.draft },
        bikeRides
    )
}


fun Application.profileRoutes() {

    val strava: Strava by inject()
    val dao: DAOS by inject()
    val db: DB by inject()
    val stravaBikeSync: StravaBikeSync by inject()

    val log = KtorSimpleLogger("ProfileRoutes")

    routing {

        val userRepository: UserRepository by inject()

        authenticate {

            get<ProfilePath> { r ->
                apiResult {
                    val authUserId = authUserId()
                    println("Profile for userId: $authUserId")
                    authUserId?.let { userId ->
                        dao.profileDao.getByUserId(userId)
                    }
                }
            }

            put<ProfilePath> { r ->
                val update = call.receive<Profile>()
                apiResult {
                    val authUserId = authUserId()
                    authUserId?.let { userId ->
                        return@let dao.profileDao.getByUserId(userId)?.let {
                            it.firstname = update.firstname
                            it.lastname = update.lastname
                            update.profilePhotoUrl?.let { newPhotoUrl ->
                                it.profilePhotoUrl = newPhotoUrl
                            }
                            if (it.createdAt == null) {
                                it.createdAt = LocalDateTime.now().formatAsIsoDate()
                            }
                            dao.profileDao.update(it.oid(), it)
                            dao.profileDao.getByUserId(userId)
                        }
                    }
                }
            }

            get<ProfilePath.Bikes> { r ->
                apiResult {
                    val authUserId = authUserId()
                    authUserId?.let { userId ->
                        dao.bikeDao.getByUserId(userId)
                    }
                }
            }

            get<ProfilePath.Rides> { r ->
                apiResult {
                    val authUserId = authUserId()
                    authUserId?.let { userId ->
                        dao.bikeRideDao.getByUserIdPaginated(userId, r.page, r.pageSize)
                    }
                }
            }

            get<ProfilePath.PagedByKeyRides> { r ->
                apiResult {
                    val authUserId = authUserId()
                    authUserId?.let { userId ->
                        try {
                            val result = dao.bikeRideDao.getByUserIdPaginatedByKey(userId, r.key, r.pageSize)

                            result
                        } catch (e: Exception) {
                            e.printStackTrace()
                            listOf<BikeRide>()
                        }
                    }
                }
            }

            get<ProfilePath.Extended> { r ->
                apiResult {
                    val authUserId = authUserId()
                    authUserId?.let { userId ->
                        getUserProfile(dao, userId, r.draft)
                    }
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

            put<ProfilePath.Bikes.BikeById.Setup> { r ->
                try {
                    val bikeId = r.parent.bikeId
                    val bike = call.receive<Bike>()

                    log.debug("Setup bike $bikeId")

                    apiResult {
                        SetupBikeUseCase(dao.bikeDao, dao.bikeRideDao).invoke(bikeId, bike)
                    }
                } catch (e: Exception) {
                    log.error("Error", e)
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

            put<ProfilePath.Setup> {
                val update = call.receive<SetupProfileUpdate>()
                apiResult {
                    val authUserId = authUserId()
                    authUserId?.let { userId ->

                        val user = dao.userDao.getById(authUserId)

                        dao.profileDao.getByUserId(userId)?.let {
                            it.firstname = update.firstName
                            it.lastname = update.lastName
                            update.profilePhotoUrl?.let { newPhotoUrl ->
                                it.profilePhotoUrl = newPhotoUrl
                            }

                            if (it.createdAt == null) {
                                it.createdAt = LocalDateTime.now().formatAsIsoDate()
                            }
                            dao.profileDao.update(it.oid(), it)
                        }

                        dao.bikeDao.getByUserId(userId).forEach {
                            it.draft = it.stravaId != null && !update.synchronizedBikesIds.contains(it.oid())
                            dao.bikeDao.update(it.oid(), it)
                        }


                        val bikes = dao.bikeDao.getByUserId(userId)

                        user?.stravaAuthData?.let { authData ->
                            val stravaClient = strava.withAuth(authData);
                            val activities = stravaClient.activities(LocalDateTime.now().minusMonths(6))

                            activities?.forEach { activity ->

                                if (db.activities.findOne(StravaActivity::id eq activity.id) == null) {

                                    val syncStravaBikeIds = bikes.filter { !it.draft }.map { it.stravaId }
                                    val supported =
                                        supportedActivityTypes.contains(activity.type)
                                    if (syncStravaBikeIds.contains(activity.gearId) && supported) {
                                        db.activities.insertOne(activity)
                                        val ride = BikeRide(
                                            userId = user.oid(),
                                            stravaId = activity.id,
                                            bikeId = bikes.firstOrNull { it.stravaId == activity.gearId }?.oid(),
                                            name = activity.name,
                                            distance = activity.distance,
                                            movingTime = activity.movingTime,
                                            elapsedTime = activity.elapsedTime,
                                            dateTime = activity.startDate,
                                            totalElevationGain = activity.totalElevationGain,
                                        )
                                        dao.bikeRideDao.create(ride)
                                    }
                                }
                            }
                        }
                        getUserProfile(dao, userId, true)
                    }
                }
            }


        }
    }
}


suspend fun syncBikeActivities(bike: Bike, user: User, strava: Strava, db: DB, dao: DAOS) {


    println("Synchronizing bike activities: ${bike.oid()}")

    val stravaClient = strava.withAuth(user.stravaAuthData ?: throw Exception("User auth not found"));
    val activities = stravaClient.activities(LocalDateTime.now().minusMonths(6))

    activities?.forEach { activity ->

        if (db.activities.findOne(StravaActivity::id eq activity.id) == null) {
            val supported = supportedActivityTypes.contains(activity.type)
            if (bike.stravaId == activity.gearId && supported) {
                db.activities.insertOne(activity)
                val ride = BikeRide(
                    userId = user.oid(),
                    stravaId = activity.id,
                    bikeId = bike.oid(),
                    name = activity.name,
                    distance = activity.distance,
                    movingTime = activity.movingTime,
                    elapsedTime = activity.elapsedTime,
                    dateTime = activity.startDate,
                    totalElevationGain = activity.totalElevationGain,
                )
                dao.bikeRideDao.create(ride)
            }
        }
    }
}


suspend fun removeBikeActivities(bike: Bike, user: User, strava: Strava, db: DB, dao: DAOS) {
    println("Deleting bike activities: ${bike.oid()}")
    db.activities.deleteMany(StravaActivity::gearId eq bike.stravaId)
    db.bikeRides.deleteMany(BikeRide::bikeId eq bike.oid())
}
