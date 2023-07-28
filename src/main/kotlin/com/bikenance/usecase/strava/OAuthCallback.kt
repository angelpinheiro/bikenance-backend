package com.bikenance.usecase.strava

import com.bikenance.api.strava.AuthData
import com.bikenance.data.database.mongodb.DAOS
import com.bikenance.data.database.mongodb.DB
import com.bikenance.data.model.Profile
import com.bikenance.data.model.User
import com.bikenance.data.model.login.TokenPair
import com.bikenance.data.model.strava.AthleteStats
import com.bikenance.data.model.strava.StravaAthlete
import com.bikenance.data.network.jwt.JwtMgr
import com.bikenance.data.network.push.MessageData
import com.bikenance.data.network.push.MessageSender
import com.bikenance.data.network.push.MessageType
import com.bikenance.data.network.strava.StravaApi
import com.bikenance.data.network.strava.StravaApiForUser
import com.bikenance.data.repository.UserRepository
import com.bikenance.usecase.SyncStravaDataUseCase
import com.bikenance.util.bknLogger
import kotlinx.coroutines.*


class StravaAuthCallbackHandler(
    val strava: StravaApi,
    val db: DB,
    val dao: DAOS,
    val userRepository: UserRepository,
    val syncStravaDataUseCase: SyncStravaDataUseCase,
    val messageSender: MessageSender,
    private val jwtMgr: JwtMgr,
) {

    val log = bknLogger("StravaAuthCallbackHandler")
    val scope = CoroutineScope(Job() + Dispatchers.IO)

    suspend fun handleCallback(auth: AuthData): TokenPair {

        log.info("Received auth callback ${auth.accessToken}")

        val stravaClient = strava.withAuth(auth);
        val stravaAthlete: StravaAthlete = stravaClient.athlete().successOrFail("Could not get athlete info")
        val stravaStats: AthleteStats =
            stravaClient.athleteStats(stravaAthlete.id).successOrFail("Could not get athlete stats")

        val loggedUser: User = when (val user = dao.userDao.getByAthleteId(stravaAthlete.id)) {
            null -> {
                val newUser = dao.userDao.create(User(username = null, password = null, athleteId = stravaAthlete.id, stravaAuthData = auth))
                    ?: throw Exception("Could not create user")
                createUserProfile(newUser, stravaAthlete, stravaStats, stravaClient)
                newUser
            }

            else -> {
                dao.userDao.update(user.oid(), user.copy(athleteId = stravaAthlete.id, stravaAuthData = auth))
                dao.userDao.getById(user.oid()) ?: throw Exception("Could not update user")
            }
        }

        val tokens = jwtMgr.generator.generateTokenPair(loggedUser)
        loggedUser.refreshToken = tokens.refreshToken
        userRepository.update(loggedUser.oid(), loggedUser)
        return tokens
    }

    private suspend fun createUserProfile(
        newUser: User,
        stravaAthlete: StravaAthlete,
        stats: AthleteStats,
        stravaClient: StravaApiForUser
    ) {

        // save strava athlete info
        dao.stravaAthleteDao.create(stravaAthlete)

        dao.profileDao.create(
            Profile(
                userId = newUser.oid(),
                username = stravaAthlete.username,
                firstname = stravaAthlete.firstname,
                lastname = stravaAthlete.lastname,
                sex = stravaAthlete.sex,
                profilePhotoUrl = stravaAthlete.profile,
                athleteStats = stats
            )
        )

        syncStravaDataUseCase.invoke(newUser.oid(), stravaClient)
    }
}








