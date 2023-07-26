package com.bikenance

import com.bikenance.api.strava.createAuth
import com.bikenance.data.network.strava.StravaApi
import com.bikenance.data.network.strava.StravaApiForUser
import com.bikenance.util.bknLogger
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class StravaApiTest : KoinTest {

    private val log = bknLogger("StravaApiTest")


    @Test
    fun testRoot() = testApplication {
        val response = client.get("/ping")
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun testStravaApi() = testApplication {

        application {

            loadConfig()

            val accessToken = environment.config.property("strava.testing.access_token").getString()
            val username = environment.config.property("strava.testing.username").getString()

            val stravaApi: StravaApi by inject()
            val api = testApi(stravaApi, accessToken)

            runBlocking {
                val athlete = api.athlete().successOrFail()
                assertEquals(athlete.username, username)
                assertNotNull(athlete.bikeRefs)
                athlete.bikeRefs?.map {
                    val bike = api.bike(it.id).successOrFail()
                    assertEquals(bike.id, it.id)
                }

                val activities = api.activitiesPaginated(1, 10).successOrFail()
                assertEquals(activities.size, 10)
            }
        }
    }

    @After
    fun tearDown() {
        stopKoin()
    }
}

fun testApi(stravaApi: StravaApi, accessToken: String): StravaApiForUser {
    return stravaApi.withAuth(
        createAuth(
            accessToken = accessToken,
            refreshToken = null,
            expiresIn = 10000
        )
    )
}
