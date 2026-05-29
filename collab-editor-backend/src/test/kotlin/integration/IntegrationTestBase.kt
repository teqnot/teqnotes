package integration

import com.example.config.DatabaseConfig
import com.example.features.auth.data.model.RefreshTokens
import com.example.features.auth.data.model.Users
import com.example.features.friends.data.local.Friendships
import com.example.features.notes.data.local.NoteAccesses
import com.example.features.notes.data.local.Notes
import com.example.features.notes.data.local.ProjectMembers
import com.example.features.notes.data.local.Projects
import com.example.module
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.testing.ApplicationTestBuilder
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import kotlin.test.DefaultAsserter.assertEquals

@Testcontainers
abstract class IntegrationTestBase {

    companion object {
        @Container
        @JvmStatic
        val postgres: PostgreSQLContainer<*> = PostgreSQLContainer(
            DockerImageName.parse("postgres:16-alpine")
        ).apply {
            withDatabaseName("test_db")
            withUsername("test_user")
            withPassword("test_password")
            withLogConsumer { }
        }
    }

    data class TestTokens(val owner: String, val friend: String, val friendId: Int = 2)

    protected suspend fun ApplicationTestBuilder.setupTestEnvironment(): SetupResult {
        System.setProperty("DATABASE_URL", postgres.jdbcUrl)
        System.setProperty("DATABASE_USER", postgres.username)
        System.setProperty("DATABASE_PASSWORD", postgres.password)
        System.setProperty("DATABASE_DRIVER", postgres.driverClassName)

        DatabaseConfig.init()

        clearDatabase()

        application {
            module()
        }

        val client = createClient {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true; isLenient = true })
            }
            expectSuccess = false
        }

        println("Registering test users...")
        registerUser(client, "owner@test.com", "password123", "Owner")
        registerUser(client, "friend@test.com", "password123", "Friend")

        println("Logging in...")
        val tokenOwner = login(client, "owner@test.com", "password123")
        val tokenFriend = login(client, "friend@test.com", "password123")

        if (tokenOwner.isEmpty() || tokenFriend.isEmpty()) {
            throw IllegalStateException("Failed to obtain auth tokens for tests")
        }

        val ownerId = getCurrentUserId(client, tokenOwner) ?: 1
        val friendId = getCurrentUserId(client, tokenFriend) ?: 2

        println("Setup complete: owner=$ownerId, friend=$friendId")

        return SetupResult(client, TestTokens(tokenOwner, tokenFriend, friendId))
    }

    protected suspend fun ApplicationTestBuilder.clearDatabase() {
        transaction {
            try {
                exec("SET session_replication_role = replica")

                exec("TRUNCATE TABLE note_accesses, project_members, friendships, notes, projects, refresh_tokens RESTART IDENTITY CASCADE")
                exec("TRUNCATE TABLE users RESTART IDENTITY CASCADE")

                exec("SET session_replication_role = DEFAULT")

                println("Database cleared successfully")
            } catch (e: Exception) {
                println("TRUNCATE failed, falling back to DELETE: ${e.message}")
                exec("DELETE FROM note_accesses")
                exec("DELETE FROM project_members")
                exec("DELETE FROM friendships")
                exec("DELETE FROM notes")
                exec("DELETE FROM projects")
                exec("DELETE FROM refresh_tokens")
                exec("DELETE FROM users")
            }
        }
    }

    protected data class SetupResult(
        val client: HttpClient,
        val tokens: TestTokens
    )

    protected fun HttpRequestBuilder.auth(token: String) {
        header("Authorization", "Bearer $token")
    }

    private suspend fun registerUser(client: HttpClient, email: String, password: String, name: String): Boolean {
        val response = client.post("/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("email" to email, "password" to password, "name" to name))
        }

        if (!response.status.isSuccess()) {
            println("Register failed for $email: ${response.status} - ${response.bodyAsText()}")
            return false
        }
        return true
    }

    private suspend fun login(client: HttpClient, email: String, password: String): String {
        val response = client.post("/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("email" to email, "password" to password))
        }

        val body = response.bodyAsText()

        if (!response.status.isSuccess()) {
            println("Login failed for $email: ${response.status} - $body")
            return ""
        }

        if (body.isBlank()) {
            println("❌ Empty response body from /auth/login for $email")
            return ""
        }

        return try {
            val token = Json.parseToJsonElement(body).jsonObject["accessToken"]?.jsonPrimitive?.content ?: ""
            if (token.isEmpty()) {
                println("❌ No accessToken in response for $email: $body")
            }
            token
        } catch (e: Exception) {
            println("Failed to parse JSON for $email: $body")
            println("   Exception: ${e.message}")
            ""
        }
    }

    protected suspend fun sendFriendRequest(client: HttpClient, token: String, friendEmail: String): Int? {
        val response = client.post("/friends/request") {
            contentType(ContentType.Application.Json)
            auth(token)
            setBody("""{"friendEmail":"$friendEmail"}""")
        }

        val body = response.bodyAsText()
        println("Friend request response: ${response.status} - $body")

        if (!response.status.isSuccess()) {
            return null
        }

        return try {
            Json.parseToJsonElement(body).jsonObject["id"]?.jsonPrimitive?.content?.toIntOrNull()
        } catch (e: Exception) {
            println("Failed to parse friend request response: $body")
            null
        }
    }

    protected suspend fun getCurrentUserId(client: HttpClient, token: String): Int? {
        return try {
            val parts = token.split(".")
            if (parts.size >= 2) {
                val payload = String(java.util.Base64.getUrlDecoder().decode(parts[1]))
                val json = Json.parseToJsonElement(payload).jsonObject
                json["sub"]?.jsonPrimitive?.content?.toIntOrNull()
            } else null
        } catch (e: Exception) {
            println("⚠️ Failed to parse token for user ID: ${e.message}")
            null
        }
    }

    protected suspend fun extractToken(response: HttpResponse): String {
        return Json.parseToJsonElement(response.bodyAsText()).jsonObject["accessToken"]?.jsonPrimitive?.content
            ?: throw IllegalStateException("No accessToken in response")
    }

    protected suspend fun HttpClient.createProject(token: String, name: String, description: String? = null): Int {
        val response = post("/projects") {
            contentType(ContentType.Application.Json)
            auth(token)
            setBody("""{"name":"$name","description":${description?.let { "\"$it\"" } ?: "null"}}""")
        }
        return Json.parseToJsonElement(response.bodyAsText()).jsonObject["id"]?.jsonPrimitive?.content?.toIntOrNull()
            ?: throw IllegalStateException("Failed to create project: ${response.bodyAsText()}")
    }

    protected suspend fun HttpClient.createNote(token: String, title: String, content: String, projectId: Int? = null): Int {
        val response = post("/notes") {
            contentType(ContentType.Application.Json)
            auth(token)
            setBody("""{"title":"$title","content":"$content","projectId":$projectId}""")
        }
        return Json.parseToJsonElement(response.bodyAsText()).jsonObject["id"]?.jsonPrimitive?.content?.toIntOrNull()
            ?: throw IllegalStateException("Failed to create note: ${response.bodyAsText()}")
    }

    protected suspend fun assertStatus(response: HttpResponse, expected: HttpStatusCode, context: String = "") {
        assertEquals(expected, response.status, "$context failed: ${response.bodyAsText()}")
    }
}