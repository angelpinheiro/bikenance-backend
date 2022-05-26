package com.bikenance.routing

import com.bikenance.Articles
import com.bikenance.model.User
import com.bikenance.repository.UserRepository
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.routing
import kotlinx.serialization.*

@Serializable
@Resource("/users")
class Users(val filter: String? = null){
    @Serializable
    @Resource("{id}")
    class Id(val parent: Users = Users(), val id: Int)
}


fun Application.userRoutes() {

    val userRepository = UserRepository()

    routing {

        get<Users> { r ->
            call.respond(userRepository.findAll())
        }

        get<Users.Id> {r ->
            val u = userRepository.findById(r.id)
            call.respond(u ?: "User not found")
        }

        put<Users.Id> {r ->
            val user = call.receive<User>()
            val u = userRepository.updateUser(r.id, user)
            call.respond(u ?: "User not found")
        }
    }
}
