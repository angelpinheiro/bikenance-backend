package com.bikenance.database.tables

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Table

/**
 * TABLES
 */
object AthletesTable : IntIdTable() {
    var athleteId = varchar("athlete_id", 50)
    var username = varchar("username", 50)
    var firstname =  varchar("first_name", 50)
    var lastname = varchar("last_name", 50)
}

object BikesTable: IntIdTable() {
    var name = varchar("name", 50)
}

object AthleteBikesTable : Table() {
    var athlete = reference("athlete", AthletesTable)
    var bike = reference("bike", BikesTable)
}

/**
 *  ENTITIES
 */
//
class AthleteEntity(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<AthleteEntity>(AthletesTable)
    var athleteId by AthletesTable.athleteId
    var firstname by AthletesTable.firstname
    var lastname by AthletesTable.lastname
    var username by AthletesTable.username
}

class BikeEntity(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<BikeEntity>(BikesTable)
    var name by BikesTable.name
}
