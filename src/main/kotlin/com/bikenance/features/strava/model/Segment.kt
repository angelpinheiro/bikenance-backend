package com.bikenance.features.strava.model

import com.fasterxml.jackson.annotation.JsonProperty


data class Segment(

    @JsonProperty("id")
    var id: Int? = null,
    @JsonProperty("resource_state")
    var resourceState: Int? = null,
    @JsonProperty("name")
    var name: String? = null,
    @JsonProperty("activity_type")
    var activityType: String? = null,
    @JsonProperty("distance")
    var distance: Double? = null,
    @JsonProperty("average_grade")
    var averageGrade: Double? = null,
    @JsonProperty("maximum_grade")
    var maximumGrade: Double? = null,
    @JsonProperty("elevation_high")
    var elevationHigh: Double? = null,
    @JsonProperty("elevation_low")
    var elevationLow: Double? = null,
    @JsonProperty("start_latlng")
    var startLatlng: ArrayList<Double> = arrayListOf(),
    @JsonProperty("end_latlng")
    var endLatlng: ArrayList<Double> = arrayListOf(),
    @JsonProperty("climb_category")
    var climbCategory: Int? = null,
    @JsonProperty("city")
    var city: String? = null,
    @JsonProperty("state")
    var state: String? = null,
    @JsonProperty("country")
    var country: String? = null,
    @JsonProperty("private")
    var private: Boolean? = null,
    @JsonProperty("hazardous")
    var hazardous: Boolean? = null,
    @JsonProperty("starred")
    var starred: Boolean? = null

)