package com.bikenance.database

import com.bikenance.database.DatabaseFactory.dbQuery
import com.bikenance.model.User
import com.bikenance.model.Users
import org.jetbrains.exposed.sql.*


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

    override suspend fun createUser(username: String, password: String): User? = dbQuery {
        val insertStatement = Users.insert {
            it[Users.username] = username
            it[Users.password] = password
        }
        insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToArticle)
    }

    override suspend fun editUser(id: Int, user: User): Boolean = dbQuery {
        Users.update({ Users.id eq id }) {
            it[username] = user.username
            it[password] = user.password
        } > 0
    }

    override suspend fun deleteUser(id: Int): Boolean = dbQuery {
        Users.deleteWhere { Users.id eq id } > 0
    }

    private fun resultRowToArticle(row: ResultRow) = User(
        id = row[Users.id],
        username = row[Users.username],
        password = "*"
    )
}