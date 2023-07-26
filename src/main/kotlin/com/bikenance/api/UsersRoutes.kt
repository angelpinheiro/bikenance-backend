package com.bikenance.api

import com.bikenance.data.database.mongodb.DAOS
import com.bikenance.data.model.UserUpdate
import com.bikenance.data.network.push.MessageData
import com.bikenance.data.network.push.MessageSender
import com.bikenance.data.network.push.MessageType
import com.bikenance.data.repository.UserRepository
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.put
import io.ktor.server.routing.*
import kotlinx.coroutines.*
import kotlinx.serialization.Serializable
import org.koin.ktor.ext.inject

@Serializable
@Resource("/users")
class Users(val filter: String? = null) {
    @Serializable
    @Resource("{id}")
    class Id(val parent: Users = Users(), val id: String)

    @Serializable
    @Resource("messagingToken")
    class MessagingToken(val parent: Users = Users())

    @Serializable
    @Resource("/u/{username}")
    class Username(val parent: Users = Users(), val username: String)
}


fun Route.userRoutes() {

    val scope = CoroutineScope(Job() + Dispatchers.IO)

    val userRepository: UserRepository by inject()
    val daos: DAOS by inject()
    val messageSender: MessageSender by inject()

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

    put<Users.MessagingToken> { r ->
        val tokenWrapper = call.receive<TokenWrapper>()
        authApiResult { userId ->


            userRepository.getById(userId)?.let {user ->

                val differentToken = user.firebaseToken != tokenWrapper.token

                user.firebaseToken = tokenWrapper.token
                userRepository.update(userId, user)

                daos.profileDao.getByUserId(userId)?.let { profile ->
                    if (profile.sync && differentToken) {
                        scope.launch {
                            messageSender.sendMessage(
                                user, MessageData(
                                    MessageType.PROFILE_SYNC
                                )
                            )
                        }
                    }
                }
                true
            }
        }
    }
}

data class TokenWrapper(
    val token: String
)