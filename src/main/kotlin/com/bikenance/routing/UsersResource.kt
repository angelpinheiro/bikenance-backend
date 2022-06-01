package com.bikenance.routing

import com.bikenance.model.UserUpdate
import com.bikenance.repository.UserRepository
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.put
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.koin.ktor.ext.inject

@Serializable
@Resource("/users")
class Users(val filter: String? = null) {
    @Serializable
    @Resource("{id}")
    class Id(val parent: Users = Users(), val id: String)

    @Serializable
    @Resource("/u/{username}")
    class Username(val parent: Users = Users(), val username: String)
}

@Serializable
@Resource("/profile")
class Profile()


fun Application.userRoutes() {

    routing {

        val userRepository: UserRepository by inject()

        authenticate {

            get<Users> { r ->
                apiResult {
                    when (r.filter) {
                        null -> userRepository.findAll()
                        else -> userRepository.filter(r.filter)
                    }
                }
            }

            get<Users.Id> { r ->
                apiResult { userRepository.getById(r.id) }
            }

            get<Users.Username> { r ->
                apiResult { userRepository.getByUsername(r.username) }
            }

            put<Users.Id> { r ->
                val user = call.receive<UserUpdate>()
                apiResult {
                    userRepository.update(r.id, user)
                }
            }

            get<Profile> { r ->
                apiResult {
                    authUserId()?.let {
                        userRepository.getById(it)
                    }
                }
            }
        }
    }
}
