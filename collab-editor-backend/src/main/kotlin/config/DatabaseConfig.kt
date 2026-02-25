package com.example.config

import com.example.model.RefreshTokens
import com.example.model.Users
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseConfig {

    fun init() {
        val dbUrl = System.getenv("DATABASE_URL")
            ?: "jdbc:postgresql://localhost:5432/editor_db"

        val dbUser = System.getenv("DATABASE_USER")
            ?: "editor_user"

        val dbPassword = System.getenv("DATABASE_PASSWORD")
            ?: "editor_password"

        val dbDriver = System.getenv("DATABASE_DRIVER")
            ?: "org.postgresql.Driver"

        println("Connecting to database: $dbUrl")

        Database.connect(
            url = dbUrl,
            driver = dbDriver,
            user = dbUser,
            password = dbPassword
        )

        transaction {
            connection.autoCommit = false

            SchemaUtils.createMissingTablesAndColumns(
                Users,
                RefreshTokens
            )

            println("DB Initialized successfully")
            println("Tables created: users, refresh_tokens")
        }
    }
}