package com.bikenance.api

import com.bikenance.data.database.mongodb.DB
import com.bikenance.data.model.strava.StravaAthlete
import io.ktor.resources.*
import io.ktor.server.resources.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.koin.ktor.ext.inject
import org.litote.kmongo.eq
import org.litote.kmongo.findOne

@Serializable
@Resource("/athletes")
class Athletes(val filter: String? = null) {
    @Serializable
    @Resource("{id}")
    class Id(val parent: Athletes = Athletes(), val id: String)

    @Serializable
    @Resource("/u/{userId}")
    class UserId(val parent: Athletes = Athletes(), val userId: String)
}


fun Route.athleteRoutes() {

    val db: DB by inject()

    get<Athletes> {
        apiResult { db.athletes.find().toList() }
    }

    get<Athletes.Id> { r ->
        apiResult { db.athletes.findOne(StravaAthlete::id eq r.id) }
    }

}
