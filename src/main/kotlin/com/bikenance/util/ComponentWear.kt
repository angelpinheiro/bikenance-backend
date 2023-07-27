package com.bikenance.util

import com.bikenance.data.model.components.BikeComponent
import com.bikenance.data.model.components.RevisionFrequency
import com.bikenance.data.model.components.RevisionUnit
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

fun wearByElapsedTime(from: LocalDateTime?, freq: RevisionFrequency, now: LocalDateTime = LocalDateTime.now()): Double {

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


fun BikeComponent.determineWear(
    freq: RevisionFrequency
): Double {


    return when (freq.unit) {
        RevisionUnit.KILOMETERS -> {
            wearByKm(usage.distance, freq.every.toDouble())
        }

        RevisionUnit.HOURS -> {
            wearByHours(usage.duration, freq.every.toDouble())
        }

        RevisionUnit.WEEKS, RevisionUnit.MONTHS -> {
            wearByElapsedTime(from, freq)
        }
    }
}