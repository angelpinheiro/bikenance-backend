package com.bikenance.strava.usecase

import com.bikenance.database.mongodb.DAOS
import com.bikenance.database.mongodb.DB
import com.bikenance.login.config.JwtMgr
import com.bikenance.model.Bike
import com.bikenance.model.Profile
import com.bikenance.model.User
import com.bikenance.strava.AuthData
import com.bikenance.strava.model.StravaAthlete


class StravaOAuthCallbackHandler(val strava: com.bikenance.strava.api.Strava, val db: DB, val dao: DAOS, private val jwtMgr: JwtMgr) {

    suspend fun handleCallback(auth: AuthData): String {

        val stravaClient = strava.withAuth(auth);
        val stravaAthlete: StravaAthlete = stravaClient.athlete() ?: throw Exception("Athlete not found")

        val loggedUser: User = when (val user = dao.userDao.getByAthleteId(stravaAthlete.id)) {
            null -> {
                val newUser = dao.userDao.create(User(null, null, stravaAthlete.id, auth)) ?: throw Exception("Could not create user")
                createUserProfile(newUser, stravaAthlete, stravaClient)
                newUser
            }
            else -> {
                dao.userDao.update(user.oid(), user.copy(athleteId = stravaAthlete.id, authData = auth))
                user
            }
        }
        return jwtMgr.generator.generateToken(loggedUser, stravaAthlete, auth)
    }

    private suspend fun createUserProfile(newUser: User, stravaAthlete: StravaAthlete, stravaClient: com.bikenance.strava.api.StravaApiForUser) {

        // save strava athlete info
        dao.stravaAthleteDao.create(stravaAthlete)

        dao.profileDao.create(
            Profile(
                userId = newUser.oid(),
                username = stravaAthlete.username,
                firstname = stravaAthlete.firstname,
                lastname = stravaAthlete.lastname,
                sex = stravaAthlete.sex,
                profilePhotoUrl = stravaAthlete.profile
            )
        )

        // get and create athlete bikes
        val bikes = stravaAthlete.bikeRefs?.mapNotNull { ref ->
            stravaClient.bike(ref.id)?.let { gear ->
                println("Bike details for id ${ref.id}: ${gear.name}")
                val bike = Bike(
                    name = ref.name,
                    brandName = gear.brandName,
                    modelName = gear.modelName,
                    distance = gear.distance,
                    userId = newUser.oid(),
                    stravaId = ref.id,
                    draft = true

                )
                dao.bikeDao.create(bike)
            }
        }
    }
}








