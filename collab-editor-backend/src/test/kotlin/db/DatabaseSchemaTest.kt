package db

import com.example.model.Blocks
import com.example.model.Friendships
import com.example.model.NoteAccesses
import com.example.model.Notes
import com.example.model.ProjectMembers
import com.example.model.Projects
import com.example.model.RefreshTokens
import com.example.model.TeamMembers
import com.example.model.Teams
import com.example.model.Users
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class DatabaseSchemaTest {

    companion object {
        @BeforeAll
        @JvmStatic
        fun setup() {
            Database.Companion.connect("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;", driver = "org.h2.Driver")

            transaction {
                SchemaUtils.createMissingTablesAndColumns(
                    Users,
                    RefreshTokens,
                    Teams,
                    Friendships,
                    Projects,
                    ProjectMembers,
                    Notes,
                    NoteAccesses,
                    Blocks,
                    TeamMembers
                )
            }
        }

        @AfterAll
        @JvmStatic
        fun tearDown() {
            // H2 auto clears
        }
    }

    @Test
    fun `all tables should be created successfully`() {
        transaction {
            Assertions.assertTrue(true)
        }
    }
}