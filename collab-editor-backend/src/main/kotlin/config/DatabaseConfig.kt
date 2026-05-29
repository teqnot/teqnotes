package com.example.config

import com.example.features.friends.data.local.Friendships
import com.example.features.notes.data.local.NoteAccesses
import com.example.features.notes.data.local.Notes
import com.example.features.notes.data.local.ProjectMembers
import com.example.features.notes.data.local.Projects
import com.example.features.auth.data.model.RefreshTokens
import com.example.features.auth.data.model.Users
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseConfig {

    fun init() {
        val dbUrl = System.getProperty("DATABASE_URL")
            ?: System.getenv("DATABASE_URL")
            ?: "jdbc:postgresql://localhost:5433/editor_db"

        val dbUser = System.getProperty("DATABASE_USER")
            ?: System.getenv("DATABASE_USER")
            ?: "editor_user"

        val dbPassword = System.getProperty("DATABASE_PASSWORD")
            ?: System.getenv("DATABASE_PASSWORD")
            ?: "editor_password"

        val dbDriver = System.getProperty("DATABASE_DRIVER")
            ?: System.getenv("DATABASE_DRIVER")
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
                RefreshTokens,
                Friendships,
                Projects,
                ProjectMembers,
                Notes,
                NoteAccesses
            )

            println("DB Initialized successfully")
        }
    }
}