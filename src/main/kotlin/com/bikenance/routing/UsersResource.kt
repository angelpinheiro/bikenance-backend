package com.bikenance.routing

import com.bikenance.model.User
import com.bikenance.repository.UserRepository
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.put
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.*
import org.koin.ktor.ext.inject

@Serializable
@Resource("/users")
class Users(val filter: String? = null){
    @Serializable
    @Resource("{id}")
    class Id(val parent: Users = Users(), val id: String)
}


fun Application.userRoutes() {

    routing {

        val userRepository: UserRepository by inject()

        authenticate {

            get<Users> { r ->
                when(r.filter) {
                    null -> call.respond(userRepository.findAll())
                    else -> call.respond(userRepository.filter(r.filter))
                }
            }

            get<Users.Id> { r ->
                val u = userRepository.getById(r.id)

                when(val token = u?.authData) {
                    null -> call.respond(u ?: "User not found")
                    else -> call.respond(u)
                }


            }

            put<Users.Id> { r ->
                val user = call.receive<User>()
                val u = userRepository.update(r.id, user)
                call.respond(u ?: "User not found")
            }
        }
    }
}
