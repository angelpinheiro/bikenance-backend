package com.bikenance.api.profile

import io.ktor.resources.*
import kotlinx.serialization.Serializable


@Serializable
@Resource("/profile")
class ProfilePath {

    @Serializable
    @Resource("/setup")
    class Setup(val parent: ProfilePath = ProfilePath())

    @Serializable
    @Resource("/rides")
    class Rides(val parent: ProfilePath = ProfilePath(), val page: Int = 0, val pageSize: Int = 10)

    @Serializable
    @Resource("/pagedRides")
    class PagedByKeyRides(val parent: ProfilePath = ProfilePath(), val pageSize: Int = 10, val key: String? = null)

    @Serializable
    @Resource("/sync")
    class SyncBikes(val parent: ProfilePath = ProfilePath())

    @Serializable
    @Resource("/bikes")
    class Bikes(val parent: ProfilePath = ProfilePath()) {

        @Serializable
        @Resource("{bikeId}")
        class BikeById(val parent: Bikes = Bikes(), val bikeId: String) {
            @Serializable
            @Resource("setup")
            class Setup(val parent: BikeById)
            @Serializable
            @Resource("components")
            class Components(val parent: BikeById)
        }
    }


}