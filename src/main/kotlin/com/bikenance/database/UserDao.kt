package com.bikenance.database

import com.bikenance.database.DatabaseFactory.dbQuery
import com.bikenance.database.tables.Users
import com.bikenance.model.User
import com.bikenance.model.UserUpdate
import org.jetbrains.exposed.sql.*


class UserDao : UserDaoFacade {

    override suspend fun user(id: Int): User? = dbQuery {
        Users.select { Users.id eq id }
            .map(::resultRowToArticle)
            .singleOrNull()
    }

    override suspend fun user(username: String): User? = dbQuery {
        Users.select { Users.username eq username }
            .map(::resultRowToArticle)
            .singleOrNull()
    }

    override suspend fun allUsers(): List<User> = dbQuery {
        Users.selectAll().map(::resultRowToArticle)
    }

    override suspend fun filter(pattern: String): List<User> = dbQuery {
        Users.select { Users.username like "%$pattern%" }
            .map(::resultRowToArticle)
            .toList()
    }

    override suspend fun createUser(username: String, password: String): User? = dbQuery {
        val insertStatement = Users.insert {
            it[Users.username] = username
            it[Users.password] = password
        }
        insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToArticle)
    }

    override suspend fun createUser(user: User): User? = dbQuery {
        val insertStatement = Users.insert { newUser ->
            newUser[username] = user.username
            newUser[password] = user.password
            user.athleteId?.let { newUser[athleteId] = it }
            user.athleteToken?.let { newUser[athleteToken] = it }
        }
        insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToArticle)
    }

    override suspend fun editUser(id: Int, user: UserUpdate): Boolean = dbQuery {
        Users.update({ Users.id eq id }) { u ->
            user.username?.let { u[username] = it }
            user.password?.let { u[password] = it }
            user.athleteId?.let { u[athleteId] = it }
            user.athleteToken?.let { u[athleteToken] = it }
        } > 0
    }

    override suspend fun deleteUser(id: Int): Boolean = dbQuery {
        Users.deleteWhere { Users.id eq id } > 0
    }

    private fun resultRowToArticle(row: ResultRow) = User(
        id = row[Users.id],
        username = row[Users.username],
        password = row[Users.password],
        athleteId = row[Users.athleteId],
        athleteToken = row[Users.athleteToken],
    )
}