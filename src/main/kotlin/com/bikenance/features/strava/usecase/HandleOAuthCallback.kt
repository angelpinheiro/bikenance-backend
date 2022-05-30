package com.bikenance.features.strava.usecase

import com.bikenance.database.mongodb.DB
import com.bikenance.features.strava.api.Strava
import com.bikenance.features.strava.model.StravaAthlete
import com.bikenance.model.User
import org.litote.kmongo.*

suspend fun handleOAuthCallback(strava: Strava, db: DB, authToken: String) {


    val stravaAthlete = strava.withToken(authToken).athlete()

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
}




