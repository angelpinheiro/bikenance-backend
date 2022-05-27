package com.bikenance.database.tables

import com.bikenance.database.tables.AthletesTable.nullable
import com.bikenance.database.tables.BikesTable.nullable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Table

/**
 * TABLES
 */
object AthletesTable : IntIdTable() {
    val userId = integer("user_id")
    var athleteToken = text("athlete_token").nullable()
    var athleteId = varchar("athlete_id", 50)
    var username = varchar("username", 50).nullable()
    var firstname = varchar("first_name", 50).nullable()
    var lastname = varchar("last_name", 50).nullable()
    var city = text("city").nullable()
    var state = text("state").nullable()
    var sex = varchar("sex", 10).nullable()
    var profilePhotoUrl = text("profile_photo_url").nullable()
    var athleteType = integer("athlete_type").nullable()
    var datePreference = varchar("date_pref", 20).nullable()
    var measurementPreference = varchar("measurement_preference", 10).nullable()
    var weight = integer("weight").nullable()
}

object BikesTable : IntIdTable() {
    var name = varchar("name", 50)
    var stravaId = varchar("stravaId", 50).nullable()
    var primary = bool("primary").nullable()
    var distance = integer("distance").nullable()
}

object AthleteBikesTable : Table() {
    var athlete = reference("athlete", AthletesTable)
    var bike = reference("bike", BikesTable)
}

/**
 *  ENTITIES
 */
//
class AthleteEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<AthleteEntity>(AthletesTable)
    var userId by AthletesTable.userId
    var athleteToken by AthletesTable.athleteToken
    var athleteId by AthletesTable.athleteId
    var firstname by AthletesTable.firstname
    var lastname by AthletesTable.lastname
    var username by AthletesTable.username
    var city by AthletesTable.city
    var state by AthletesTable.state
    var sex by AthletesTable.sex
    var profilePhotoUrl by AthletesTable.profilePhotoUrl
    var athleteType by AthletesTable.athleteType
    var datePreference by AthletesTable.datePreference
    var measurementPreference by AthletesTable.measurementPreference
    var weight by AthletesTable.weight
}

class BikeEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<BikeEntity>(BikesTable)

    var name by BikesTable.name
}
