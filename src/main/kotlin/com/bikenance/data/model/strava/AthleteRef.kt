package com.bikenance.data.model.strava

import com.fasterxml.jackson.annotation.JsonProperty

data class AthleteRef(

    @JsonProperty("id")
    var id: String,
    @JsonProperty("resource_state")
    var resourceState: Int? = null

)