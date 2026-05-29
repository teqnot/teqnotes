package integration

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.testApplication
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class FriendRouteTest : IntegrationTestBase() {

    @Test
    fun `accept friend request - success`() = runTest {
        testApplication {
            val (client, tokens) = setupTestEnvironment()

            println("Sending friend request from owner to friend...")
            val reqId = sendFriendRequest(client, tokens.owner, "friend@test.com")
                ?: return@testApplication fail("Failed to send friend request")

            println("Friend request sent with ID: $reqId")

            println("Friend accepting request...")
            val acceptResponse = client.post("/friends/accept/$reqId") {
                auth(tokens.friend)
            }

            println("Accept response: ${acceptResponse.status} - ${acceptResponse.bodyAsText()}")
            assertEquals(HttpStatusCode.OK, acceptResponse.status, "Accept failed: ${acceptResponse.bodyAsText()}")

            println("Checking friends list...")
            val friendsResponse = client.get("/friends") {
                auth(tokens.owner)
            }

            println("Friends response: ${friendsResponse.status} - ${friendsResponse.bodyAsText()}")
            assertEquals(HttpStatusCode.OK, friendsResponse.status)
            assertTrue(friendsResponse.bodyAsText().contains("friend@test.com"), "Friend not found in list")
        }
    }

    @Test
    fun `cannot accept someone else's request`() = runTest {
        testApplication {
            val (client, tokens) = setupTestEnvironment()

            val requestResponse = client.post("/friends/request") {
                contentType(ContentType.Application.Json)
                auth(tokens.owner)
                setBody("""{"friendEmail":"friend@test.com"}""")
            }
            val reqId = Json.parseToJsonElement(requestResponse.bodyAsText()).jsonObject["id"]?.jsonPrimitive?.content?.toIntOrNull()
                ?: return@testApplication fail("Request failed")

            val selfAccept = client.post("/friends/accept/$reqId") {
                auth(tokens.owner)
            }

            assertEquals(HttpStatusCode.Forbidden, selfAccept.status)
        }
    }

    @Test
    fun `send request to non-existent user - 404`() = runTest {
        testApplication {
            val (client, tokens) = setupTestEnvironment()

            val response = client.post("/friends/request") {
                contentType(ContentType.Application.Json)
                auth(tokens.owner)
                setBody("""{"friendEmail":"nonexistent@test.com"}""")
            }

            assertEquals(HttpStatusCode.NotFound, response.status)
            assertTrue(response.bodyAsText().contains("not found"))
        }
    }

    @Test
    fun `send request to yourself - 400`() = runTest {
        testApplication {
            val (client, tokens) = setupTestEnvironment()

            val response = client.post("/friends/request") {
                contentType(ContentType.Application.Json)
                auth(tokens.owner)
                setBody("""{"friendEmail":"owner@test.com"}""")
            }

            assertEquals(HttpStatusCode.BadRequest, response.status)
            assertTrue(response.bodyAsText().contains("cannot befriend yourself"))
        }
    }

    @Test
    fun `send duplicate request - 400`() = runTest {
        testApplication {
            val (client, tokens) = setupTestEnvironment()

            val first = client.post("/friends/request") {
                contentType(ContentType.Application.Json)
                auth(tokens.owner)
                setBody("""{"friendEmail":"friend@test.com"}""")
            }
            assertEquals(HttpStatusCode.Created, first.status)

            val second = client.post("/friends/request") {
                contentType(ContentType.Application.Json)
                auth(tokens.owner)
                setBody("""{"friendEmail":"friend@test.com"}""")
            }

            assertEquals(HttpStatusCode.BadRequest, second.status)
            assertTrue(second.bodyAsText().contains("already pending") || second.bodyAsText().contains("already friends"))
        }
    }

    @Test
    fun `accept non-existent request - 404`() = runTest {
        testApplication {
            val (client, tokens) = setupTestEnvironment()

            val response = client.post("/friends/accept/99999") {
                auth(tokens.friend)
            }

            assertEquals(HttpStatusCode.NotFound, response.status)
        }
    }

    @Test
    fun `reject friend request - success`() = runTest {
        testApplication {
            val (client, tokens) = setupTestEnvironment()

            val requestResponse = client.post("/friends/request") {
                contentType(ContentType.Application.Json)
                auth(tokens.owner)
                setBody("""{"friendEmail":"friend@test.com"}""")
            }
            val reqId = Json.parseToJsonElement(requestResponse.bodyAsText()).jsonObject["id"]?.jsonPrimitive?.content?.toIntOrNull()
                ?: return@testApplication fail("Request failed")

            val rejectResponse = client.post("/friends/reject/$reqId") {
                auth(tokens.friend)
            }

            assertEquals(HttpStatusCode.OK, rejectResponse.status)

            val requestsResponse = client.get("/friends/requests") {
                auth(tokens.friend)
            }
            assertEquals(HttpStatusCode.OK, requestsResponse.status)
            assertFalse(requestsResponse.bodyAsText().contains("\"id\":$reqId"))
        }
    }

    @Test
    fun `get friends list when empty - 200 with empty array`() = runTest {
        testApplication {
            val (client, tokens) = setupTestEnvironment()

            val response = client.get("/friends") {
                auth(tokens.owner)
            }

            assertEquals(HttpStatusCode.OK, response.status)
            val json = Json.parseToJsonElement(response.bodyAsText())
            assertTrue(json is kotlinx.serialization.json.JsonArray || json.jsonObject["friends"]?.jsonArray != null)
        }
    }

    @Test
    fun `get incoming requests - shows pending`() = runTest {
        testApplication {
            val (client, tokens) = setupTestEnvironment()

            client.post("/friends/request") {
                contentType(ContentType.Application.Json)
                auth(tokens.owner)
                setBody("""{"friendEmail":"friend@test.com"}""")
            }

            val response = client.get("/friends/requests") {
                auth(tokens.friend)
            }

            assertEquals(HttpStatusCode.OK, response.status)
            assertTrue(response.bodyAsText().contains("owner@test.com"))
        }
    }

    @Test
    fun `friend cannot see private note before sharing`() = runTest {
        testApplication {
            val (client, tokens) = setupTestEnvironment()

            val noteId = client.createNote(tokens.owner, "Private", "Secret content")

            val response = client.get("/notes/$noteId") {
                auth(tokens.friend)
            }

            assertTrue(response.status in listOf(HttpStatusCode.Forbidden, HttpStatusCode.NotFound))
        }
    }

    @Test
    fun `full flow - request - accept - share - access`() = runTest {
        testApplication {
            val (client, tokens) = setupTestEnvironment()

            val requestResponse = client.post("/friends/request") {
                contentType(ContentType.Application.Json)
                auth(tokens.owner)
                setBody("""{"friendEmail":"friend@test.com"}""")
            }
            val reqId = Json.parseToJsonElement(requestResponse.bodyAsText()).jsonObject["id"]?.jsonPrimitive?.content?.toIntOrNull()
                ?: return@testApplication fail("Request failed")

            val acceptResponse = client.post("/friends/accept/$reqId") {
                auth(tokens.friend)
            }
            assertEquals(HttpStatusCode.OK, acceptResponse.status)

            val noteId = client.createNote(tokens.owner, "Shared", "Now accessible")

            val shareResponse = client.post("/notes/$noteId/share") {
                contentType(ContentType.Application.Json)
                auth(tokens.owner)
                setBody("""{"targetUserId":${tokens.friendId},"role":"READ_WRITE"}""")
            }
            assertEquals(HttpStatusCode.OK, shareResponse.status)

            val getResponse = client.get("/notes/$noteId") { auth(tokens.friend) }
            assertEquals(HttpStatusCode.OK, getResponse.status)

            val editResponse = client.patch("/notes/$noteId") {
                contentType(ContentType.Application.Json)
                auth(tokens.friend)
                setBody("""{"content":"Edited by friend"}""")
            }
            assertEquals(HttpStatusCode.OK, editResponse.status)
        }
    }
}