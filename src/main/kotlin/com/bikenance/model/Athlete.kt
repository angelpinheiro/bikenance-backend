package com.bikenance.model

import com.fasterxml.jackson.annotation.JsonProperty

data class AthleteVO(
    @JsonProperty("id") var id: String? = null,
    @JsonProperty("username") var username: String? = null,
    @JsonProperty("firstname") var firstname: String? = null,
    @JsonProperty("lastname") var lastname: String? = null,
    @JsonProperty("city") var city: String? = null,
    @JsonProperty("state") var state: String? = null
)