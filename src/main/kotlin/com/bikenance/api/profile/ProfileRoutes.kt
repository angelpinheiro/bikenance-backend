package com.bikenance.api.profile

import com.bikenance.api.apiResult
import com.bikenance.api.authUserId
import com.bikenance.data.database.mongodb.DAOS
import com.bikenance.data.database.mongodb.DB
import com.bikenance.data.model.BikeRide
import com.bikenance.data.model.ExtendedProfile
import com.bikenance.data.model.Profile
import com.bikenance.data.model.SetupProfileUpdate
import com.bikenance.data.model.strava.StravaActivity
import com.bikenance.data.network.stravaApi.Strava
import com.bikenance.data.network.stravaApi.supportedActivityTypes
import com.bikenance.data.repository.UserRepository
import com.bikenance.usecase.strava.StravaBikeSync
import com.bikenance.util.bknLogger
import com.bikenance.util.formatAsIsoDate
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.put
import io.ktor.server.routing.*
import io.ktor.util.logging.*
import org.koin.ktor.ext.inject
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import java.time.LocalDateTime


suspend fun getUserProfile(dao: DAOS, userId: String, includeDraftBikes: Boolean): ExtendedProfile {
    val profile = dao.profileDao.getByUserId(userId)
    val bikes = dao.bikeDao.getByUserId(userId)
    val bikeRides = dao.bikeRideDao.getByUserId(userId)

    return ExtendedProfile(
        profile, if (includeDraftBikes) bikes else bikes.filter { !it.draft }, bikeRides
    )
}


fun Route.profileRoutes() {

    val log = bknLogger("ProfileRoutes")

    val strava: Strava by inject()
    val dao: DAOS by inject()
    val db: DB by inject()

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
                            val supported = supportedActivityTypes.contains(activity.type)
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
