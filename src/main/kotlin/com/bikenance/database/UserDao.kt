package com.bikenance.database

import com.bikenance.database.DatabaseFactory.dbQuery
import com.bikenance.model.User
import com.bikenance.model.UserUpdate
import com.bikenance.model.Users
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like


class UserDao : UserDaoFacade {

    override suspend fun user(id: Int): User? = dbQuery {
        Users.select { Users.id eq id}
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
        Users.select { Users.username like "%$pattern%"}
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

    override suspend fun editUser(id: Int, user: UserUpdate): Boolean = dbQuery {
        Users.update({ Users.id eq id }) { u ->
            user.username?.let {u[username] = it }
            user.password?.let {u[password] = it }
            user.stravaToken?.let {u[stravaToken] = it }
        } > 0
    }

    override suspend fun deleteUser(id: Int): Boolean = dbQuery {
        Users.deleteWhere { Users.id eq id } > 0
    }

    private fun resultRowToArticle(row: ResultRow) = User(
        id = row[Users.id],
        username = row[Users.username],
        stravaToken = row[Users.stravaToken],
        password = row[Users.password]
    )
}