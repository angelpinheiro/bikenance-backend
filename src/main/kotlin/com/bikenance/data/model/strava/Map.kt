package com.bikenance.data.model.strava

import com.fasterxml.jackson.annotation.JsonProperty


data class Map(

    @JsonProperty("id")
    var id: String? = null,
    @JsonProperty("polyline")
    var polyline: String? = null,
    @JsonProperty("resource_state")
    var resourceState: Int? = null,
    @JsonProperty("summary_polyline")
    var summaryPolyline: String? = null

)