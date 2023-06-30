package com.bikenance.routing

import com.bikenance.database.mongodb.DAOS
import com.bikenance.database.mongodb.DB
import com.bikenance.model.*
import com.bikenance.repository.UserRepository
import com.bikenance.strava.model.StravaActivity
import com.bikenance.strava.usecase.StravaBikeSync
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.resources.put
import io.ktor.server.routing.*
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
        class Bike(val parent: Bikes = Bikes(), val bikeId: String)

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

    val strava: com.bikenance.strava.api.Strava by inject()
    val dao: DAOS by inject()
    val db: DB by inject()
    val stravaBikeSync: StravaBikeSync by inject()

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
                        }catch (e: Exception) {
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

            get<ProfilePath.Bikes.Bike> { r ->
                apiResult {
                    dao.bikeDao.getById(r.bikeId)
                }
            }

            put<ProfilePath.Bikes.Bike> { r ->
                val bike = call.receive<Bike>()
                val authUserId = authUserId()

                apiResult {

                    val user = dao.userDao.getById(authUserId ?: "") ?: throw Exception("User not found")
                    val old = dao.bikeDao.getById(r.bikeId) ?: throw Exception("Bike not found")

                    dao.bikeDao.update(r.bikeId, bike)
                    val updated = dao.bikeDao.getById(r.bikeId) ?: throw Exception("Bike not found")


                    // bike is being synchronized
                    if (old.draft && !bike.draft) {
                        stravaBikeSync.onBikeAdded(user, updated)
                    }
                    // bike is being removed
                    else if (bike.draft && !old.draft) {
                        stravaBikeSync.onBikeRemoved(user, updated)
                    }

                    dao.bikeDao.getById(r.bikeId)

                }
            }

            delete<ProfilePath.Bikes.Bike> { r ->
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
                val syncBikes = call.receive<SyncBikes>()
                println("ProfilePath.SyncBikes: ${syncBikes.synchronizedBikesIds.joinToString(",")}")
                apiResult {
                    val authUserId = authUserId()
                    authUserId?.let { userId ->
                        val user = dao.userDao.getById(authUserId) ?: throw Exception("User not found")
                        val bikes = dao.bikeDao.getByUserId(userId)
                        val sync = syncBikes.synchronizedBikesIds

                        bikes.forEach {
                            if (it.draft && sync.contains(it.oid())) {
                                it.draft = false
                                dao.bikeDao.update(it.oid(), it)
                                stravaBikeSync.onBikeAdded(user, it)
                            }
                            // bike is being removed
                            else if (!it.draft && !sync.contains(it.oid())) {
                                it.draft = true
                                dao.bikeDao.update(it.oid(), it)
                                stravaBikeSync.onBikeRemoved(user, it)
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
                                        com.bikenance.strava.api.supportedActivityTypes.contains(activity.type)
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


suspend fun syncBikeActivities(bike: Bike, user: User, strava: com.bikenance.strava.api.Strava, db: DB, dao: DAOS) {


    println("Synchronizing bike activities: ${bike.oid()}")

    val stravaClient = strava.withAuth(user.stravaAuthData ?: throw Exception("User auth not found"));
    val activities = stravaClient.activities(LocalDateTime.now().minusMonths(6))

    activities?.forEach { activity ->

        if (db.activities.findOne(StravaActivity::id eq activity.id) == null) {
            val supported = com.bikenance.strava.api.supportedActivityTypes.contains(activity.type)
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


suspend fun removeBikeActivities(bike: Bike, user: User, strava: com.bikenance.strava.api.Strava, db: DB, dao: DAOS) {
    println("Deleting bike activities: ${bike.oid()}")
    db.activities.deleteMany(StravaActivity::gearId eq bike.stravaId)
    db.bikeRides.deleteMany(BikeRide::bikeId eq bike.oid())
}
