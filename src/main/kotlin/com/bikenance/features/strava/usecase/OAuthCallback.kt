package com.bikenance.features.strava.usecase

import com.bikenance.database.mongodb.DB
import com.bikenance.features.strava.AuthData
import com.bikenance.features.strava.api.Strava
import com.bikenance.features.strava.model.StravaAthlete
import com.bikenance.model.User
import org.litote.kmongo.*

suspend fun handleOAuthCallback(strava: Strava, db: DB, auth: AuthData) {

    val stravaClient = strava.withAuth(auth);
    val stravaAthlete = stravaClient.athlete() //TODO may be null

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
    stravaAthlete.detailedGear = stravaAthlete.bikeRefs?.map { ref ->
        stravaClient.bike(ref.id)
    }
    if (ath == null) {
        db.athletes.insertOne(stravaAthlete)
    } else {
        db.athletes.updateOneById(ath._id, stravaAthlete)
    }

    // get activities from the last month

//    stravaClient.activities(LocalDateTime.now().minusMonths(1)).forEach { activity ->
//        if (db.activities.findOne(StravaActivity::id eq activity.id) == null)
//            db.activities.insertOne(activity)
//    }

}




