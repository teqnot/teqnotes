package com.example.features.auth.data.repository

import com.example.features.auth.data.model.User
import com.example.features.auth.domain.model.UserData
import com.example.features.auth.data.model.Users
import org.jetbrains.exposed.sql.transactions.transaction

interface UserRepository {
    fun findByEmail(email: String): UserData?
    fun findById(id: Int): UserData?
    fun create(email: String, passwordHash: String, name: String): UserData
    fun delete(id: Int): Boolean
    fun existsByEmail(email: String): Boolean
}

object UserRepositoryImpl : UserRepository {

    override fun findByEmail(email: String): UserData? = transaction {
        User.find { Users.email eq email }.firstOrNull()?.let { toUserData(it) }
    }

    override fun findById(id: Int): UserData? = transaction {
        User.findById(id)?.let { toUserData(it) }
    }

    override fun create(email: String, passwordHash: String, name: String): UserData = transaction {
        val user = User.new {
            this.email = email
            this.passwordHash = passwordHash
            this.name = name
        }
        toUserData(user)
    }

    override fun delete(id: Int): Boolean = transaction {
        User.findById(id)?.let { it.delete(); true } ?: false
    }

    override fun existsByEmail(email: String): Boolean = transaction {
        User.find { Users.email eq email }.empty().not()
    }

    private fun toUserData(user: User): UserData = UserData(
        id = user.id.value,
        email = user.email,
        name = user.name,
        passwordHash = user.passwordHash,
        createdAt = user.createdAt
    )
}