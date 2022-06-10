package com.bikenance.features.strava.usecase

import com.bikenance.database.mongodb.DAOS
import com.bikenance.database.mongodb.DB
import com.bikenance.features.login.config.JwtMgr
import com.bikenance.features.strava.AuthData
import com.bikenance.features.strava.api.Strava
import com.bikenance.features.strava.api.StravaApiForUser
import com.bikenance.features.strava.model.StravaActivity
import com.bikenance.features.strava.model.StravaAthlete
import com.bikenance.model.Bike
import com.bikenance.model.BikeRide
import com.bikenance.model.Profile
import com.bikenance.model.User
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import java.time.LocalDateTime


class StravaOAuthCallbackHandler(val strava: Strava, val db: DB, val dao: DAOS, private val jwtMgr: JwtMgr) {


    /**
     * -> auth
     * 1) Get user from auth
     * 2) If user exist, do nothing
     * 3) If not:
     *  - Create user
     *  - Create user profile (with data from strava)
     *  - Get user bikes from strava
     *  - Get last user activities from strava
     *
     */

//    // TODO Refactor
//    suspend fun handleOAuthCallback(auth: AuthData): String? {
//
//        val stravaClient = strava.withAuth(auth);
//        val stravaAthlete = stravaClient.athlete() //TODO may be null
//
//        if (stravaAthlete != null) {
//            when (val u = dao.userDao.getByAthleteId(stravaAthlete.id)) {
//                null -> {
//                    dao.userDao.create(
//                        User(
//                            stravaAthlete.username ?: stravaAthlete.firstname ?: "None",
//                            ".",
//                            stravaAthlete.id,
//                            auth
//                        )
//                    )
//
//                }
//                else -> {
//                    dao.userDao.update(u.id(), u.copy(athleteId = stravaAthlete.id, authData = auth))
//                }
//            }
//
//            val ath = dao.stravaAthleteDao.getByAthleteId(stravaAthlete.id)
//            // get detailed gear
//            stravaAthlete.detailedGear = stravaAthlete.bikeRefs?.mapNotNull { ref ->
//                stravaClient.bike(ref.id)?.apply {
//                    this.name = ref.name
//                }
//
//            }
//            if (ath == null) {
//                dao.stravaAthleteDao.create(stravaAthlete)
//            } else {
//                db.athletes.updateOneById(ath._id, stravaAthlete)
//            }
//
//            // get activities from the last month
//
//            stravaClient.activities(LocalDateTime.now().minusMonths(1))?.forEach { activity ->
//                if (db.activities.findOne(StravaActivity::id eq activity.id) == null)
//                    db.activities.insertOne(activity)
//            }
//            // return a token
//            val user = db.users.findOne(User::athleteId eq stravaAthlete.id)
//            return user?.let { jwtMgr.generator.generateToken(it, stravaAthlete, auth) }
//        } else {
//            return null
//        }
//
//    }


    suspend fun handleCallback(auth: AuthData): String {

        val stravaClient = strava.withAuth(auth);
        val stravaAthlete: StravaAthlete = stravaClient.athlete() ?: throw Exception("Athlete not found")

        val loggedUser: User = when (val user = dao.userDao.getByAthleteId(stravaAthlete.id)) {
            null -> {
                val newUser = dao.userDao.create(User(null, null, stravaAthlete.id, auth))
                if (newUser == null) {
                    throw Exception("Could not create user")
                }
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

    private suspend fun createUserProfile(newUser: User, stravaAthlete: StravaAthlete, stravaClient: StravaApiForUser) {

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

//        // get and create last strava activities
//        stravaClient.activities(LocalDateTime.now().minusMonths(1))?.forEach { activity ->
//            if (db.activities.findOne(StravaActivity::id eq activity.id) == null) {
//                db.activities.insertOne(activity)
//                val ride = BikeRide(
//                    userId = newUser.oid(),
//                    stravaId = activity.id,
//                    bikeId = bikes?.firstOrNull { it.stravaId == activity.gearId }?.oid(),
//                    name = activity.name,
//                    distance = activity.distance,
//                    movingTime = activity.movingTime,
//                    elapsedTime = activity.elapsedTime,
//                    dateTime = activity.startDate, //TODO Set
//                    totalElevationGain = activity.totalElevationGain,
//                )
//                dao.bikeRideDao.create(ride)
//            }
//        }
    }
}








