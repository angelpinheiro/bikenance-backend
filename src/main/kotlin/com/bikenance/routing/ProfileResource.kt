package com.bikenance.routing

import com.bikenance.database.mongodb.DAOS
import com.bikenance.database.mongodb.DB
import com.bikenance.features.strava.model.StravaAthlete
import com.bikenance.model.ExtendedProfile
import com.bikenance.repository.UserRepository
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.resources.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.koin.ktor.ext.inject
import org.litote.kmongo.eq
import org.litote.kmongo.findOne


@Serializable
@Resource("/profile")
class Profile() {
    @Serializable
    @Resource("extended")
    class Extended(val parent: Users = Users())
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
                        val profile = dao.profileDao.getByUserId(userId)
                        val bikes = dao.bikeDao.getByUserId(userId)
                        val bikeRides = dao.bikeRideDao.getByUserId(userId)

                        ExtendedProfile(profile, bikes, bikeRides)
                    }
                }
            }
        }
    }
}