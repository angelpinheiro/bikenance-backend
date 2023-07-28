package com.bikenance.util

import com.bikenance.data.model.components.*
import com.bikenance.usecase.estimateNextMaintenanceDate
import java.time.Duration
import java.time.LocalDateTime


fun wearByKm(distanceInMeters: Double, freqInKm: Double): Double {
    return if (distanceInMeters > 0) {
        (distanceInMeters / 1000) / freqInKm
    } else {
        0.0
    }
}

fun wearByHours(durationInSeconds: Double, freqInHours: Double): Double {
    val durationHours = (durationInSeconds / 3600)
    return if (durationHours > 0) {
        durationHours / freqInHours
    } else {
        0.0
    }
}

fun wearByElapsedTime(from: LocalDateTime?, freq: RevisionFrequency, now: LocalDateTime): Double {

    if (from == null || from.isAfter(now)) {
        return 0.0
    }

    val maxDuration = when (freq.unit) {
        RevisionUnit.WEEKS -> {
            Duration.between(from, from.plusMonths(freq.every.toLong()))
        }

        RevisionUnit.MONTHS -> {
            Duration.between(from, from.plusMonths(freq.every.toLong()))
        }

        else -> {
            throw Exception("Invalid RevisionUnit ${freq.unit} (only WEEKS or MONTHS allowed)")
        }
    }

    val actualDuration = Duration.between(from, now)
    return actualDuration.toDays().toDouble() / maxDuration.toDays()
}


fun Maintenance.determineWear(untilDateTime: LocalDateTime): Double {
    return lastMaintenanceDate?.let { determineWear(it, usageSinceLast, defaultFrequency, untilDateTime) } ?: 0.0
}


fun Maintenance.updateWear(until: LocalDateTime): Maintenance {
    val status = determineWear(until)
    val estimatedDate = lastMaintenanceDate?.let {
        estimateNextMaintenanceDate(it, status, until)
    }
    return copy(
        status = status,
        estimatedDate = estimatedDate
    )
}

fun determineWear(
    lastMaintenanceDate: LocalDateTime, usageSinceLast: Usage, freq: RevisionFrequency, untilDateTime: LocalDateTime
): Double {


    return when (freq.unit) {
        RevisionUnit.KILOMETERS -> {
            wearByKm(usageSinceLast.distance, freq.every.toDouble())
        }

        RevisionUnit.HOURS -> {
            wearByHours(usageSinceLast.duration, freq.every.toDouble())
        }

        RevisionUnit.WEEKS, RevisionUnit.MONTHS -> {
            wearByElapsedTime(lastMaintenanceDate, freq, untilDateTime)
        }
    }
}