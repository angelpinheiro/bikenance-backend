package com.bikenance.features.strava.model

import com.fasterxml.jackson.annotation.JsonProperty

data class AthleteRef(

    @JsonProperty("id")
    var id: Int? = null,
    @JsonProperty("resource_state")
    var resourceState: Int? = null

)