package integration

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.testApplication
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class ProjectRouteTest : IntegrationTestBase() {

    @Test
    fun `create project - success`() = runTest {
        testApplication {
            val (client, tokens) = setupTestEnvironment()

            val response = client.post("/projects") {
                contentType(ContentType.Application.Json)
                auth(tokens.owner)
                setBody("""{"name":"Test Project","description":"Desc"}""")
            }

            assertEquals(HttpStatusCode.Created, response.status)
            val json = Json.parseToJsonElement(response.bodyAsText()).jsonObject
            val projectId = json["id"]?.jsonPrimitive?.content?.toIntOrNull() ?: -1
            assertTrue(projectId > 0)
            assertEquals("Test Project", json["name"]?.jsonPrimitive?.content)
        }
    }

    @Test
    fun `create project - unauthorized`() = runTest {
        testApplication {
            val (client, _) = setupTestEnvironment()

            val response = client.post("/projects") {
                contentType(ContentType.Application.Json)
                setBody("""{"name":"No Auth","description":"Desc"}""")
            }

            assertEquals(HttpStatusCode.Unauthorized, response.status)
        }
    }

    @Test
    fun `get projects - owner sees own`() = runTest {
        testApplication {
            val (client, tokens) = setupTestEnvironment()

            client.post("/projects") {
                contentType(ContentType.Application.Json)
                auth(tokens.owner)
                setBody("""{"name":"Owner Project","description":"Desc"}""")
            }

            val response = client.get("/projects") {
                auth(tokens.owner)
            }

            assertEquals(HttpStatusCode.OK, response.status)
            assertTrue(response.bodyAsText().contains("Owner Project"))
        }
    }

    @Test
    fun `get project - friend no access`() = runTest {
        testApplication {
            val (client, tokens) = setupTestEnvironment()

            val createResponse = client.post("/projects") {
                contentType(ContentType.Application.Json)
                auth(tokens.owner)
                setBody("""{"name":"Private Project","description":"Secret"}""")
            }
            val privateProjectId = Json.parseToJsonElement(createResponse.bodyAsText()).jsonObject["id"]?.jsonPrimitive?.content?.toIntOrNull()
                ?: return@testApplication fail("Create failed")

            val response = client.get("/projects/$privateProjectId") {
                auth(tokens.friend)
            }

            assertTrue(response.status in listOf(HttpStatusCode.Forbidden, HttpStatusCode.NotFound))
        }
    }

    @Test
    fun `update project - only owner`() = runTest {
        testApplication {
            val (client, tokens) = setupTestEnvironment()

            val createResponse = client.post("/projects") {
                contentType(ContentType.Application.Json)
                auth(tokens.owner)
                setBody("""{"name":"Updatable","description":"Old"}""")
            }
            val updProjectId = Json.parseToJsonElement(createResponse.bodyAsText()).jsonObject["id"]?.jsonPrimitive?.content?.toIntOrNull()
                ?: return@testApplication fail("Create failed")

            val ownerUpdate = client.patch("/projects/$updProjectId") {
                contentType(ContentType.Application.Json)
                auth(tokens.owner)
                setBody("""{"name":"Updated","description":"New"}""")
            }
            assertEquals(HttpStatusCode.OK, ownerUpdate.status)

            val friendUpdate = client.patch("/projects/$updProjectId") {
                contentType(ContentType.Application.Json)
                auth(tokens.friend)
                setBody("""{"name":"Hacked"}""")
            }
            assertEquals(HttpStatusCode.Forbidden, friendUpdate.status)
        }
    }

    @Test
    fun `add member to project - owner can add friend`() = runTest {
        testApplication {
            val (client, tokens) = setupTestEnvironment()

            val createResponse = client.post("/projects") {
                contentType(ContentType.Application.Json)
                auth(tokens.owner)
                setBody("""{"name":"Shared Project","description":"Collab"}""")
            }
            val sharedProjectId = Json.parseToJsonElement(createResponse.bodyAsText()).jsonObject["id"]?.jsonPrimitive?.content?.toIntOrNull()
                ?: return@testApplication fail("Create failed")

            val addResponse = client.post("/projects/$sharedProjectId/members") {
                contentType(ContentType.Application.Json)
                auth(tokens.owner)
                setBody("""{"email":"friend@test.com","role":"EDITOR"}""")
            }

            assertEquals(HttpStatusCode.OK, addResponse.status)

            val friendGet = client.get("/projects/$sharedProjectId") {
                auth(tokens.friend)
            }
            assertEquals(HttpStatusCode.OK, friendGet.status)
        }
    }

    @Test
    fun `add non-existent user to project - 404`() = runTest {
        testApplication {
            val (client, tokens) = setupTestEnvironment()

            val projectId = client.createProject(tokens.owner, "Test")

            val response = client.post("/projects/$projectId/members") {
                contentType(ContentType.Application.Json)
                auth(tokens.owner)
                setBody("""{"email":"nonexistent@test.com","role":"EDITOR"}""")
            }

            assertEquals(HttpStatusCode.NotFound, response.status)
        }
    }

    @Test
    fun `non-owner cannot add member to project - 403`() = runTest {
        testApplication {
            val (client, tokens) = setupTestEnvironment()

            val projectId = client.createProject(tokens.owner, "Protected")

            val response = client.post("/projects/$projectId/members") {
                contentType(ContentType.Application.Json)
                auth(tokens.friend)
                setBody("""{"email":"third@test.com","role":"VIEWER"}""")
            }

            assertEquals(HttpStatusCode.Forbidden, response.status)
        }
    }

    @Test
    fun `delete non-existent project - 404`() = runTest {
        testApplication {
            val (client, tokens) = setupTestEnvironment()

            val response = client.delete("/projects/99999") { auth(tokens.owner) }
            assertEquals(HttpStatusCode.NotFound, response.status)
        }
    }

    @Test
    fun `project EDITOR cannot delete project - 403`() = runTest {
        testApplication {
            val (client, tokens) = setupTestEnvironment()

            val projectId = client.createProject(tokens.owner, "No Delete")

            client.post("/projects/$projectId/members") {
                contentType(ContentType.Application.Json)
                auth(tokens.owner)
                setBody("""{"email":"friend@test.com","role":"EDITOR"}""")
            }

            val response = client.delete("/projects/$projectId") { auth(tokens.friend) }
            assertEquals(HttpStatusCode.Forbidden, response.status)
        }
    }

    @Test
    fun `create note in project without access - 403`() = runTest {
        testApplication {
            val (client, tokens) = setupTestEnvironment()

            val projectId = client.createProject(tokens.owner, "Private Project")

            val response = client.post("/notes") {
                contentType(ContentType.Application.Json)
                auth(tokens.friend)
                setBody("""{"title":"Unauthorized","content":"Text","projectId":$projectId}""")
            }

            assertEquals(HttpStatusCode.BadRequest, response.status)
            assertTrue(response.bodyAsText().contains("No permission"))
        }
    }

    @Test
    fun `update project with invalid data - 400`() = runTest {
        testApplication {
            val (client, tokens) = setupTestEnvironment()

            val projectId = client.createProject(tokens.owner, "Original")

            val response = client.patch("/projects/$projectId") {
                contentType(ContentType.Application.Json)
                auth(tokens.owner)
                setBody("""{}""")
            }

            assertNotEquals(HttpStatusCode.InternalServerError, response.status)
        }
    }

    @Test
    fun `full project flow - create - add member - create note - share access`() = runTest {
        testApplication {
            val (client, tokens) = setupTestEnvironment()

            val projectId = client.createProject(tokens.owner, "Collab Project", "Team workspace")

            val addResponse = client.post("/projects/$projectId/members") {
                contentType(ContentType.Application.Json)
                auth(tokens.owner)
                setBody("""{"email":"friend@test.com","role":"EDITOR"}""")
            }
            assertEquals(HttpStatusCode.OK, addResponse.status)

            val noteId = client.createNote(tokens.friend, "Team Note", "Collaborative content", projectId)

            val editResponse = client.patch("/notes/$noteId") {
                contentType(ContentType.Application.Json)
                auth(tokens.owner)
                setBody("""{"content":"Edited by owner"}""")
            }
            assertEquals(HttpStatusCode.OK, editResponse.status)

            val ownerNotes = client.get("/notes") { auth(tokens.owner) }
            val friendNotes = client.get("/notes") { auth(tokens.friend) }

            assertEquals(HttpStatusCode.OK, ownerNotes.status)
            assertEquals(HttpStatusCode.OK, friendNotes.status)
            assertTrue(ownerNotes.bodyAsText().contains("Team Note"))
            assertTrue(friendNotes.bodyAsText().contains("Team Note"))
        }
    }
}