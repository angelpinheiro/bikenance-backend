package com.bikenance

import io.ktor.http.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlin.test.*
import io.ktor.server.testing.*
import com.bikenance.modules.*

class ApplicationTest {
    @Test
    fun testRoot() = testApplication {
        application {
            // TODO
        }
        client.get("/").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("Bikenance Server running!", bodyAsText())
        }
    }
}