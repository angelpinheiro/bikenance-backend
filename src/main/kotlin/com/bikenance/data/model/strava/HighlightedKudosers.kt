package com.bikenance.data.model.strava

import com.fasterxml.jackson.annotation.JsonProperty


data class HighlightedKudosers(

    @JsonProperty("destination_url")
    var destinationUrl: String? = null,
    @JsonProperty("display_name")
    var displayName: String? = null,
    @JsonProperty("avatar_url")
    var avatarUrl: String? = null,
    @JsonProperty("show_name")
    var showName: Boolean? = null

)