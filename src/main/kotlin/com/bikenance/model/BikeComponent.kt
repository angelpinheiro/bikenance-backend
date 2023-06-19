package com.bikenance.model

import com.bikenance.database.mongodb.MongoModel
import com.fasterxml.jackson.annotation.JsonProperty

data class BikeComponentType(
    @JsonProperty("name") var name: String? = null,
) : MongoModel<BikeComponentType>()

data class BikeComponent(
    @JsonProperty("user_id") var userId: String? = null,
    @JsonProperty("bike_id") var bikeId: String? = null,
    @JsonProperty("name") var name: String? = null,
    @JsonProperty("brand_name") var brandName: String? = null,
    @JsonProperty("model_name") var modelName: String? = null,
    @JsonProperty("distance") var distance: Long? = null,
    @JsonProperty("type") var type: BikeComponentType? = null,
    @JsonProperty("group") var group: String? = null,
) : MongoModel<BikeComponent>()


