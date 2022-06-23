package com.bikenance.features.strava.model

import com.fasterxml.jackson.annotation.JsonProperty


data class SegmentEfforts(

    @JsonProperty("id")
    var id: String? = null,
    @JsonProperty("resource_state")
    var resourceState: Int? = null,
    @JsonProperty("name")
    var name: String? = null,
    @JsonProperty("activity")
    var activity: StravaActivity? = null,
    @JsonProperty("athlete")
    var athleteRef: AthleteRef? = null,
    @JsonProperty("elapsed_time")
    var elapsedTime: Int? = null,
    @JsonProperty("moving_time")
    var movingTime: Int? = null,
    @JsonProperty("start_date")
    var startDate: String? = null,
    @JsonProperty("start_date_local")
    var startDateLocal: String? = null,
    @JsonProperty("distance")
    var distance: Double? = null,
    @JsonProperty("start_index")
    var startIndex: Int? = null,
    @JsonProperty("end_index")
    var endIndex: Int? = null,
    @JsonProperty("average_cadence")
    var averageCadence: Double? = null,
    @JsonProperty("device_watts")
    var deviceWatts: Boolean? = null,
    @JsonProperty("average_watts")
    var averageWatts: Double? = null,
    @JsonProperty("segment")
    var segment: Segment? = Segment(),
    @JsonProperty("kom_rank")
    var komRank: String? = null,
    @JsonProperty("pr_rank")
    var prRank: String? = null,
    @JsonProperty("achievements")
    var achievements: ArrayList<String> = arrayListOf(),
    @JsonProperty("hidden")
    var hidden: Boolean? = null

)