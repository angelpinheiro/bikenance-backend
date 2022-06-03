package com.bikenance.features.strava.model

import com.fasterxml.jackson.annotation.JsonProperty


data class StravaDetailedGear(

    @JsonProperty("id") var id: String? = null,
    @JsonProperty("primary") var primary: Boolean? = null,
    @JsonProperty("resource_state") var resourceState: Int? = null,
    @JsonProperty("distance") var distance: Int? = null,
    @JsonProperty("brand_name") var brandName: String? = null,
    @JsonProperty("model_name") var modelName: String? = null,
    @JsonProperty("frame_type") var frameType: Int? = null,
    @JsonProperty("description") var description: String? = null,
    @JsonProperty("name") var name: String? = null

)
