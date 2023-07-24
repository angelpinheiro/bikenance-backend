package com.bikenance.api.profile

import com.bikenance.api.authApiResult
import com.bikenance.data.database.mongodb.DAOS
import com.bikenance.data.model.ExtendedProfile
import com.bikenance.data.model.Profile
import com.bikenance.data.model.serializer.iso8061ToLocalDateTime
import com.bikenance.util.bknLogger
import com.bikenance.util.formatAsIsoDate
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.put
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
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
    val dao: DAOS by inject()

    get<ProfilePath> {
        authApiResult { userId ->
            dao.profileDao.getByUserId(userId)
        }
    }

    put<ProfilePath> {
        val update = call.receive<Profile>()
        authApiResult { userId ->

            val profile = dao.profileDao.getByUserId(userId)

            profile?.let {
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




