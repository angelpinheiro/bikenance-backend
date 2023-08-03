package com.bikenance.util

import com.bikenance.data.model.components.Maintenance
import com.bikenance.data.model.components.RevisionFrequency
import com.bikenance.data.model.components.RevisionUnit
import com.bikenance.data.model.components.Usage
import java.time.Duration
import java.time.LocalDateTime

fun wearPercentage(
    freq: RevisionFrequency,
    from: LocalDateTime,
    until: LocalDateTime,
    usage: Usage,
): Double {

    return when (freq.unit) {
        RevisionUnit.KILOMETERS -> {
            wearPercentByKm(usage.distance, freq.every.toDouble())
        }

        RevisionUnit.HOURS -> {
            wearPercentByHours(usage.duration, freq.every.toDouble())
        }

        RevisionUnit.WEEKS, RevisionUnit.MONTHS, RevisionUnit.YEARS -> {
            wearPercentByElapsedTime(from, freq, until)
        }
    }
}


fun wearPercentByKm(distanceInMeters: Double, freqInKm: Double): Double {
    return if (distanceInMeters > 0) {
        (distanceInMeters / 1000) / freqInKm
    } else {
        0.0
    }
}

fun wearPercentByHours(durationInSeconds: Double, freqInHours: Double): Double {
    val durationHours = (durationInSeconds / 3600)
    return if (durationHours > 0) {
        durationHours / freqInHours
    } else {
        0.0
    }
}

fun wearPercentByElapsedTime(
    from: LocalDateTime?, freq: RevisionFrequency, now: LocalDateTime
): Double {

    if (from == null || from.isAfter(now)) {
        return 0.0
    }

    val maxDuration = when (freq.unit) {
        RevisionUnit.WEEKS -> {
            Duration.between(from, from.plusWeeks(freq.every.toLong()))
        }

        RevisionUnit.MONTHS -> {
            Duration.between(from, from.plusMonths(freq.every.toLong()))
        }

        RevisionUnit.YEARS -> {
            Duration.between(from, from.plusYears(freq.every.toLong()))
        }

        else -> {
            throw Exception("Invalid RevisionUnit ${freq.unit} (only WEEKS, MONTHS, or YEARS allowed)")
        }
    }

    val actualDuration = Duration.between(from, now)
    return actualDuration.toDays().toDouble() / maxDuration.toDays()
}

fun Maintenance.wearPercentage(untilDateTime: LocalDateTime): Double {
    return if (this.lastMaintenanceDate != null) {
        wearPercentage(
            defaultFrequency,
            lastMaintenanceDate,
            untilDateTime,
            usageSinceLast,
        )
    } else {
        0.0
    }
}


/**
 * Calculate the expected date for the next maintenance based on the maintenance frequency
 * and current status.
 *
 * @param today The current date and time (default value is  LocalDateTime.now())
 * @return The expected date for the next maintenance, or null if there is no lastMaintenanceDate
 */
fun Maintenance.expectedNextMaintenanceDate(today: LocalDateTime = LocalDateTime.now()): LocalDateTime? {

    return when (defaultFrequency.unit) {

        RevisionUnit.WEEKS -> {
            lastMaintenanceDate?.plusWeeks(defaultFrequency.every.toLong())
        }

        RevisionUnit.MONTHS -> {
            lastMaintenanceDate?.plusMonths(defaultFrequency.every.toLong())
        }

        RevisionUnit.YEARS -> {
            lastMaintenanceDate?.plusYears(defaultFrequency.every.toLong())
        }

        /**
         * Calculate the expected maintenance date by adding the expected duration
         * to the last maintenance date (if last maintenance date is available)
         */
        RevisionUnit.HOURS, RevisionUnit.KILOMETERS -> {
            lastMaintenanceDate?.let { lastMaintenance ->
                val currentDurationInDays = Duration.between(lastMaintenance, today).toDays()
                if (status > 0.0) {
                    val expectedDurationInDays = (currentDurationInDays / status).toLong()
                    lastMaintenance.plusDays(expectedDurationInDays)
                } else {
                    null
                }
            }
        }
    }
}