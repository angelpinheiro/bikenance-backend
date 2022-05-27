package com.bikenance.database

import com.bikenance.database.tables.*
import com.bikenance.model.User
import com.bikenance.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {

    fun init() {
        val driverClassName = "org.h2.Driver"
        val jdbcURL = "jdbc:h2:file:./data/db"
        val database = Database.connect(jdbcURL, driverClassName)
        transaction(database) {
            SchemaUtils.create(Users, AthletesTable)
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }


    suspend fun populateDatabase() {
        val userRepository = UserRepository(UserDao())

        if (userRepository.findAll().isEmpty()) {
            userRepository.create(User(-1, "angel", "angel_secret"))
            userRepository.create(User(-1, "admin", "admin_secret"))


            transaction {
                AthleteEntity.new {
                    athleteId = "1234"
                    firstname = "Ángel"
                    lastname = "Piñeiro"
                    username = "roispiper"
                }
            }


        }
    }
}