package com.bikenance.features.strava.usecase

import com.bikenance.database.mongodb.DB
import com.bikenance.features.strava.api.Strava
import com.bikenance.features.strava.model.StravaActivity
import com.bikenance.features.strava.model.StravaAthlete
import com.bikenance.model.User
import org.litote.kmongo.*
import java.time.LocalDateTime

suspend fun handleOAuthCallback(strava: Strava, db: DB, authToken: String) {

    val stravaClient = strava.withToken(authToken);

    val stravaAthlete = stravaClient.athlete()

    when (val u = db.users.findOne(User::athleteId eq stravaAthlete.id)) {
        null -> {
            db.users.insertOne(
                User(
                    stravaAthlete.username ?: stravaAthlete.firstname ?: "None",
                    ".",
                    stravaAthlete.id,
                    authToken
                )
            )

        }
        else -> {
            db.users.updateOne(
                User::id eq u.id, set(
                    User::athleteId setTo stravaAthlete.id,
                    User::athleteToken setTo authToken
                )
            )
        }
    }

    val ath = db.athletes.findOne(StravaAthlete::id eq stravaAthlete.id)
    if (ath == null)
        db.athletes.insertOne(stravaAthlete)
    else {
        db.athletes.updateOneById(ath._id, stravaAthlete)
    }

    // get activities from the last month

    stravaClient.activities(LocalDateTime.now().minusMonths(1)).filter { it.type == "Ride" }.forEach { activity ->
        if (db.activities.findOne(StravaActivity::id eq activity.id) == null)
            db.activities.insertOne(activity)
    }


}




