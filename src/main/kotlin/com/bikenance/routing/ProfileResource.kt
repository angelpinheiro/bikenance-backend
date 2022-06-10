package com.bikenance.routing

import com.bikenance.database.mongodb.DAOS
import com.bikenance.database.mongodb.DB
import com.bikenance.features.strava.api.Strava
import com.bikenance.features.strava.api.supportedActivityTypes
import com.bikenance.features.strava.model.StravaActivity
import com.bikenance.features.strava.model.StravaAthlete
import com.bikenance.model.Bike
import com.bikenance.model.BikeRide
import com.bikenance.model.ExtendedProfile
import com.bikenance.model.SetupProfileUpdate
import com.bikenance.repository.UserRepository
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.put
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.koin.ktor.ext.inject
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import java.time.LocalDateTime


@Serializable
@Resource("/profile")
class Profile() {
    @Serializable
    @Resource("/extended")
    class Extended(val parent: Profile = Profile(), val draft: Boolean = false)

    @Serializable
    @Resource("/setup")
    class Setup(val parent: Profile = Profile())

    @Serializable
    @Resource("/bikes/{bikeId}")
    class Bike(val parent: Profile = Profile(), val bikeId: String)
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

    routing {

        val userRepository: UserRepository by inject()

        authenticate {

            get<Profile> { r ->
                apiResult {
                    val authUserId = authUserId()
                    authUserId?.let { userId ->
                        userRepository.getById(userId)?.let { user ->
                            db.athletes.findOne(StravaAthlete::id eq user.athleteId)
                        }
                    }
                }
            }

            get<Profile.Extended> { r ->
                apiResult {
                    val authUserId = authUserId()
                    authUserId?.let { userId ->
                        getUserProfile(dao, userId, r.draft)
                    }
                }
            }

            get<Profile.Bike> { r ->
                apiResult {
                    val authUserId = authUserId()
                    authUserId?.let { userId ->
                        dao.bikeDao.getById(r.bikeId)
                    }
                }
            }

            put<Profile.Bike> { r ->
                val bike = call.receive<Bike>()
                apiResult {
                    val authUserId = authUserId()
                    authUserId?.let { userId ->
                        dao.bikeDao.update(r.bikeId, bike)
                        dao.bikeDao.getById(r.bikeId)
                    }
                }
            }

            put<Profile.Setup> {
                val update = call.receive<SetupProfileUpdate>()
                println(update)
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
                            it.draft = !update.synchronizedBikesIds.contains(it.oid())
                            dao.bikeDao.update(it.oid(), it)
                        }


                        val bikes = dao.bikeDao.getByUserId(userId)

                        user?.authData?.let { authData ->
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
    }
}