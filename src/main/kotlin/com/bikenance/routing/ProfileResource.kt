package com.bikenance.routing

import com.bikenance.database.mongodb.DAOS
import com.bikenance.database.mongodb.DB
import com.bikenance.features.strava.model.StravaAthlete
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
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date


@Serializable
@Resource("/profile")
class Profile() {
    @Serializable
    @Resource("/extended")
    class Extended(val parent: Profile = Profile(), val draft: Boolean = false)

    @Serializable
    @Resource("/setup")
    class Setup(val parent: Profile = Profile())
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

            put<Profile.Setup> {
                val update = call.receive<SetupProfileUpdate>()
                println(update)
                apiResult {
                    val authUserId = authUserId()
                    authUserId?.let { userId ->


                        dao.profileDao.getByUserId(userId)?.let {
                            it.firstname = update.firstName
                            it.lastname = update.lastName
                            if(it.createdAt == null) {
                                it.createdAt = LocalDateTime.now().formatAsIsoDate()
                            }
                            dao.profileDao.update(it.oid(), it)
                        }

                        dao.bikeDao.getByUserId(userId).forEach {
                            it.draft = !update.synchronizedBikesIds.contains(it.oid())
                            dao.bikeDao.update(it.oid(), it)
                        }
                        getUserProfile(dao, userId, true)
                    }


                }
            }


        }
    }
}