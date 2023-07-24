package com.bikenance.api.profile

import com.bikenance.api.authApiResult
import com.bikenance.data.database.mongodb.DAOS
import com.bikenance.data.model.serializer.iso8061ToLocalDateTime
import com.bikenance.usecase.SyncStravaDataUseCase
import com.bikenance.util.bknLogger
import io.ktor.server.resources.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.profileRideRoutes() {

    val log = bknLogger("ProfileRoutes")
    val dao: DAOS by inject()
    val syncStravaDataUseCase: SyncStravaDataUseCase by inject()


    get<ProfilePath.Rides> { r ->
        authApiResult { userId ->
            dao.bikeRideDao.getByUserIdPaginated(userId, r.page, r.pageSize)
        }
    }

    get<ProfilePath.PagedByKeyRides> { r ->
        authApiResult { userId ->
            dao.bikeRideDao.getByUserIdPaginatedByKey(userId, r.key?.iso8061ToLocalDateTime(), r.pageSize)
        }
    }

    get<ProfilePath.Rides.Refresh> { r ->
        authApiResult { userId ->
            syncStravaDataUseCase.invoke(userId)
            true
        }
    }

}




