package com.bikenance.routing

import com.bikenance.database.tables.AthleteEntity
import com.bikenance.model.AthleteVO
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.transactions.transaction

@Serializable
@Resource("/athletes")
class Athletes(val filter: String? = null) {
    @Serializable
    @Resource("{id}")
    class Id(val parent: Athletes = Athletes(), val id: Int)
}


fun Application.athleteRoutes() {

    routing {
        authenticate {
            get<Athletes> { r ->
                val athletes = transaction {
                    return@transaction AthleteEntity.all().sortedBy { it.username }.map {
                        AthleteVO(
                            it.athleteId,
                            it.username,
                            it.firstname,
                            it.lastname,
                        )
                    }.toList()
                }
                call.respond(athletes)
            }
        }
    }
}
