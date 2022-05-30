package com.bikenance.features.strava.usecase

import com.bikenance.database.tables.AthleteEntity
import com.bikenance.database.tables.AthletesTable
import com.bikenance.features.strava.api.Strava
import com.bikenance.model.User
import com.bikenance.model.UserUpdate
import com.bikenance.repository.UserRepository
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

suspend fun handleOAuthCallback(strava: Strava, userRepository: UserRepository, accessToken: String) {


    val stravaAthlete = strava.withToken(accessToken).athlete()
    val authAthleteId = stravaAthlete.id!!
    var newUser = false


    val existent = userRepository.findByAthleteId(authAthleteId)


    val targetUserId: Int = if (existent == null) {
        val r = userRepository.create(
            User(
                -1,
                stravaAthlete.username ?: "None",
                stravaAthlete.username ?: "None",
                authAthleteId
            )
        )
        newUser = true
        r?.id ?: -1
    } else {
        existent.id
    }

    // update user with auth token and athlete id
    val userUpdate = UserUpdate()
    userUpdate.athleteToken = accessToken
    userUpdate.athleteId = stravaAthlete.id
    userRepository.updateUser(targetUserId, userUpdate)

    transaction {
        if (newUser) {
            println("Update $targetUserId")
            AthleteEntity.new {
                userId = targetUserId
                athleteToken = accessToken
                username = stravaAthlete.username
                firstname = stravaAthlete.firstname
                lastname = stravaAthlete.lastname
                stravaAthlete.id?.let { athleteId = it }
                stravaAthlete.profile?.let { profilePhotoUrl = it }
                stravaAthlete.athleteType?.let { athleteType = it }
                stravaAthlete.sex?.let { sex = it }
                stravaAthlete.weight?.let { weight = it }
            }
        } else {
            println("Update $targetUserId")
            AthletesTable.update({ AthletesTable.userId.eq(targetUserId) }) {
                it[userId] = targetUserId
                it[athleteToken] = accessToken
                it[username] = stravaAthlete.username
                it[firstname] = stravaAthlete.firstname
                it[lastname] = stravaAthlete.lastname
                it[sex] = stravaAthlete.sex
                it[profilePhotoUrl] = stravaAthlete.profile
            }
        }
    }
}




