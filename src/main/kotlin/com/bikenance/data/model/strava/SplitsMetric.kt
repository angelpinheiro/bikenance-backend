package com.bikenance.data.model.strava

import com.fasterxml.jackson.annotation.JsonProperty


data class SplitsMetric(

    @JsonProperty("distance")
    var distance: Double? = null,
    @JsonProperty("elapsed_time")
    var elapsedTime: Int? = null,
    @JsonProperty("elevation_difference")
    var elevationDifference: Double? = null,
    @JsonProperty("moving_time")
    var movingTime: Int? = null,
    @JsonProperty("split")
    var split: Int? = null,
    @JsonProperty("average_speed")
    var averageSpeed: Double? = null,
    @JsonProperty("pace_zone")
    var paceZone: Int? = null

)