package com.bikenance.data.network.push

import com.bikenance.AppConfig
import io.ktor.util.logging.*
import java.io.File
import java.io.FileInputStream
import java.io.InputStream

class ConfigLoader {

    private val log = KtorSimpleLogger(javaClass.simpleName)

    fun getConfigFileAsStream(configFile: String): InputStream {
        return readFirebaseConfigFromFile(configFile) ?: readFirebaseConfigAsClasspathResource(configFile)
    }

    /**
     * Reads the firebase config file from a file in some possible locations
     */
    private fun readFirebaseConfigFromFile(configFile: String): FileInputStream? {

        log.info("Loading firebase config file: $configFile")
        val possibleLocations = listOf(
            configFile,
            "src/main/resources/$configFile"
        )
        possibleLocations.forEach {
            val possibleConfigFile = File(it)
            if (possibleConfigFile.exists() && possibleConfigFile.isFile) {
                log.info("\tFirebase config file found at ${possibleConfigFile.path}")
                return FileInputStream(possibleConfigFile)
            } else {
                log.info("\tFirebase config file not found at ${possibleConfigFile.path}")
            }
        }
        log.info("Firebase config file not found in any of the possible locations")
        possibleLocations.forEach {
            log.info(" - $it")
        }
        return null
    }

    /**
     * Reads the firebase config file from a classpath resource
     */
    private fun readFirebaseConfigAsClasspathResource(configFile: String): InputStream {
        log.info("Locating firebase config file: $configFile")
        val inputStream = {}.javaClass.classLoader.getResourceAsStream(configFile)
        if (inputStream != null) {
            log.info("$configFile resource found on classpath")
            return inputStream
        } else {
            log.info("$configFile resource not found")
        }
        throw Exception("Resource $configFile not found on classpath")
    }


}