package com.bikenance.features.strava.model

import com.fasterxml.jackson.annotation.JsonProperty


data class Gear(
    @JsonProperty("id")
    var id: String? = null,
    @JsonProperty("primary")
    var primary: Boolean? = null,
    @JsonProperty("name")
    var name: String? = null,
    @JsonProperty("resource_state")
    var resourceState: Int? = null,
    @JsonProperty("distance")
    var distance: Long? = null

)