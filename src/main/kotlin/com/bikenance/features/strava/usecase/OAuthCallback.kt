package com.bikenance.features.strava.usecase

import com.bikenance.database.mongodb.DB
import com.bikenance.features.login.config.JwtMgr
import com.bikenance.features.strava.AuthData
import com.bikenance.features.strava.api.Strava
import com.bikenance.features.strava.model.StravaActivity
import com.bikenance.features.strava.model.StravaAthlete
import com.bikenance.model.User
import org.litote.kmongo.*
import java.time.LocalDateTime


class StravaOAuthCallbackHandler(strava: Strava, db: DB, private val jwtMgr: JwtMgr) {
    suspend fun handleOAuthCallback(strava: Strava, db: DB, auth: AuthData): String? {

        val stravaClient = strava.withAuth(auth);
        val stravaAthlete = stravaClient.athlete() //TODO may be null

        if (stravaAthlete != null) {
            when (val u = db.users.findOne(User::athleteId eq stravaAthlete.id)) {
                null -> {

                    db.users.insertOne(
                        User(
                            stravaAthlete.username ?: stravaAthlete.firstname ?: "None",
                            ".",
                            stravaAthlete.id,
                            auth
                        )
                    )

                }
                else -> {
                    db.users.updateOne(
                        User::_id eq u._id, set(
                            User::athleteId setTo stravaAthlete.id,
                            User::authData setTo auth
                        )
                    )
                }
            }

            val ath = db.athletes.findOne(StravaAthlete::id eq stravaAthlete.id)
            // get detailed gear
            stravaAthlete.detailedGear = stravaAthlete.bikeRefs?.mapNotNull { ref ->
                stravaClient.bike(ref.id)
            }
            if (ath == null) {
                db.athletes.insertOne(stravaAthlete)
            } else {
                db.athletes.updateOneById(ath._id, stravaAthlete)
            }

            // get activities from the last month

            stravaClient.activities(LocalDateTime.now().minusMonths(1))?.forEach { activity ->
                if (db.activities.findOne(StravaActivity::id eq activity.id) == null)
                    db.activities.insertOne(activity)
            }
            // return a token
            val user = db.users.findOne(User::athleteId eq stravaAthlete.id)
            return user?.let { jwtMgr.generator.generateToken(it, stravaAthlete, auth) }
        } else {
            return null
        }

    }
}






