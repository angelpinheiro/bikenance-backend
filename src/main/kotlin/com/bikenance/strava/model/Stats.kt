package com.bikenance.strava.model

import com.fasterxml.jackson.annotation.JsonProperty

data class AthleteStats(
    @JsonProperty("biggest_ride_distance") val biggestRideDistance: Double,
    @JsonProperty("biggest_climb_elevation_gain") val biggestClimbElevationGain: Double,
    @JsonProperty("recent_ride_totals") val recentRideTotals: ActivityTotal,
    @JsonProperty("ytd_ride_totals") val ytdRideTotals: ActivityTotal,
    @JsonProperty("all_ride_totals") val allRideTotals: ActivityTotal,
)

data class ActivityTotal(
    @JsonProperty("count") val count: Double,
    @JsonProperty("distance") val distance: Double,
    @JsonProperty("elapsed_time") val duration: Double,
    @JsonProperty("moving_time") val movingTime: Double,
    @JsonProperty("elevation_gain") val elevationGain: Double,
)