package com.bikenance.routing

import com.bikenance.database.tables.AthleteEntity
import com.bikenance.features.strava.api.Strava
import com.bikenance.model.Athlete
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.ktor.ext.inject

@Serializable
@Resource("/athletes")
class Athletes(val filter: String? = null) {
    @Serializable
    @Resource("{id}")
    class Id(val parent: Athletes = Athletes(), val id: Int)
}


fun Application.athleteRoutes() {

    val strava: Strava by inject()

    routing {
        authenticate {
            get<Athletes> { r ->
                val athletes = transaction {
                    return@transaction AthleteEntity.all().sortedBy { it.username }.map {
                        Athlete(
                            it.athleteId,
                            it.userId,
                            it.username,
                            it.firstname,
                            it.lastname,
                            profilePhotoUrl = it.profilePhotoUrl
                        )
                    }.toList()
                }
                call.respond(athletes)
            }

            get<Athletes.Id> { r ->

                when(val token = transaction { AthleteEntity.findById(r.id)?.athleteToken }) {
                    null -> call.respond("User not found")
                    else -> call.respond(strava.withToken(token).athlete())
                }


            }
        }
    }
}
