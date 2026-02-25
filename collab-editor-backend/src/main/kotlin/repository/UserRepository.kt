package com.example.repository

import com.example.model.User
import com.example.model.Users
import org.jetbrains.exposed.sql.transactions.transaction

interface UserRepository {
    fun findByEmail(email: String): User?
    fun findById(id: Int): User?
    fun create(email: String, passwordHash: String, name: String): User
    fun delete(id: Int): Boolean
    fun existsByEmail(email: String): Boolean
}

object UserRepositoryImpl : UserRepository {
    override fun findByEmail(email: String): User? = transaction {
        User.find { Users.email eq email }.firstOrNull()
    }

    override fun findById(id: Int): User? = transaction {
        User.findById(id)
    }

    override fun create(email: String, passwordHash: String, name: String): User = transaction {
        User.new {
            this.email = email
            this.passwordHash = passwordHash
            this.name = name
        }
    }

    override fun delete(id: Int): Boolean = transaction {
        val user = User.findById(id)
        user?.delete()
        user != null
    }

    override fun existsByEmail(email: String): Boolean = transaction {
        User.find { Users.email eq email }.empty().not()
    }
}