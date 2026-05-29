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

class NoteRouteTest : IntegrationTestBase() {

    @Test
    fun `share note - friend gains access`() = runTest {
        testApplication {
            val (client, tokens) = setupTestEnvironment()

            println("Creating individual note...")
            val createResponse = client.post("/notes") {
                contentType(ContentType.Application.Json)
                auth(tokens.owner)
                setBody("""{"title":"Shared Note","content":"Now public to friend","projectId":null}""")
            }

            println("Create response: ${createResponse.status} - ${createResponse.bodyAsText()}")

            val shareNoteId = try {
                Json.parseToJsonElement(createResponse.bodyAsText()).jsonObject["id"]?.jsonPrimitive?.content?.toIntOrNull()
            } catch (e: Exception) {
                println("Failed to parse note ID: ${createResponse.bodyAsText()}")
                null
            } ?: return@testApplication fail("Create failed")

            println("Note created with ID: $shareNoteId")

            println("Sharing note with friend (targetUserId=${tokens.friendId})...")
            val shareResponse = client.post("/notes/$shareNoteId/share") {
                contentType(ContentType.Application.Json)
                auth(tokens.owner)
                setBody("""{"targetUserId":${tokens.friendId},"role":"READ_WRITE"}""")
            }

            println("Share response: ${shareResponse.status} - ${shareResponse.bodyAsText()}")
            assertEquals(HttpStatusCode.OK, shareResponse.status, "Share failed: ${shareResponse.bodyAsText()}")

            println("Friend reading note...")
            val friendGet = client.get("/notes/$shareNoteId") {
                auth(tokens.friend)
            }
            println("Friend get response: ${friendGet.status}")
            assertEquals(HttpStatusCode.OK, friendGet.status, "Friend cannot read shared note")

            println("Friend editing note...")
            val friendUpdate = client.patch("/notes/$shareNoteId") {
                contentType(ContentType.Application.Json)
                auth(tokens.friend)
                setBody("""{"content":"Edited by friend"}""")
            }
            println("Friend update response: ${friendUpdate.status} - ${friendUpdate.bodyAsText()}")
            assertEquals(HttpStatusCode.OK, friendUpdate.status, "Friend cannot edit shared note")
        }
    }

    @Test
    fun `project access overrides individual share`() = runTest {
        testApplication {
            val (client, tokens) = setupTestEnvironment()

            val projectResponse = client.post("/projects") {
                contentType(ContentType.Application.Json)
                auth(tokens.owner)
                setBody("""{"name":"Priority Test","description":"Project access > individual"}""")
            }
            val projId = Json.parseToJsonElement(projectResponse.bodyAsText()).jsonObject["id"]?.jsonPrimitive?.content?.toIntOrNull()
                ?: return@testApplication fail("Project create failed")

            client.post("/projects/$projId/members") {
                contentType(ContentType.Application.Json)
                auth(tokens.owner)
                setBody("""{"email":"friend@test.com","role":"VIEWER"}""")
            }

            val noteResponse = client.post("/notes") {
                contentType(ContentType.Application.Json)
                auth(tokens.owner)
                setBody("""{"title":"Project Note","content":"In project","projectId":$projId}""")
            }
            val projNoteId = Json.parseToJsonElement(noteResponse.bodyAsText()).jsonObject["id"]?.jsonPrimitive?.content?.toIntOrNull()
                ?: return@testApplication fail("Note create failed")

            val friendGet = client.get("/notes/$projNoteId") {
                auth(tokens.friend)
            }
            assertEquals(HttpStatusCode.OK, friendGet.status)

            val friendUpdate = client.patch("/notes/$projNoteId") {
                contentType(ContentType.Application.Json)
                auth(tokens.friend)
                setBody("""{"content":"Try to edit"}""")
            }
            assertEquals(HttpStatusCode.Forbidden, friendUpdate.status)
        }
    }

    @Test
    fun `create note with invalid project ID - 404`() = runTest {
        testApplication {
            val (client, tokens) = setupTestEnvironment()

            val response = client.post("/notes") {
                contentType(ContentType.Application.Json)
                auth(tokens.owner)
                setBody("""{"title":"Bad Project","content":"Text","projectId":99999}""")
            }

            assertEquals(HttpStatusCode.BadRequest, response.status)
            assertTrue(response.bodyAsText().contains("No permission") || response.bodyAsText().contains("not found"))
        }
    }

    @Test
    fun `update non-existent note - 404`() = runTest {
        testApplication {
            val (client, tokens) = setupTestEnvironment()

            val response = client.patch("/notes/99999") {
                contentType(ContentType.Application.Json)
                auth(tokens.owner)
                setBody("""{"title":"Updated"}""")
            }

            assertEquals(HttpStatusCode.NotFound, response.status)
        }
    }

    @Test
    fun `delete non-existent note - 404`() = runTest {
        testApplication {
            val (client, tokens) = setupTestEnvironment()

            val response = client.delete("/notes/99999") {
                auth(tokens.owner)
            }

            assertEquals(HttpStatusCode.NotFound, response.status)
        }
    }

    @Test
    fun `friend with READ_ONLY cannot edit note`() = runTest {
        testApplication {
            val (client, tokens) = setupTestEnvironment()

            val noteId = client.createNote(tokens.owner, "Read Only", "Content")

            client.post("/notes/$noteId/share") {
                contentType(ContentType.Application.Json)
                auth(tokens.owner)
                setBody("""{"targetUserId":${tokens.friendId},"role":"READ_ONLY"}""")
            }

            val response = client.patch("/notes/$noteId") {
                contentType(ContentType.Application.Json)
                auth(tokens.friend)
                setBody("""{"content":"Hacked"}""")
            }

            assertEquals(HttpStatusCode.Forbidden, response.status)
        }
    }

    @Test
    fun `owner can always edit their note`() = runTest {
        testApplication {
            val (client, tokens) = setupTestEnvironment()

            val noteId = client.createNote(tokens.owner, "Original", "Content")

            val response = client.patch("/notes/$noteId") {
                contentType(ContentType.Application.Json)
                auth(tokens.owner)
                setBody("""{"content":"Updated by owner"}""")
            }

            assertEquals(HttpStatusCode.OK, response.status)

            val getResponse = client.get("/notes/$noteId") { auth(tokens.owner) }
            assertTrue(getResponse.bodyAsText().contains("Updated by owner"))
        }
    }

    @Test
    fun `project EDITOR can edit project notes`() = runTest {
        testApplication {
            val (client, tokens) = setupTestEnvironment()

            val projectId = client.createProject(tokens.owner, "Editor Test")
            client.post("/projects/$projectId/members") {
                contentType(ContentType.Application.Json)
                auth(tokens.owner)
                setBody("""{"email":"friend@test.com","role":"EDITOR"}""")
            }

            val noteId = client.createNote(tokens.owner, "Project Note", "Content", projectId)

            val response = client.patch("/notes/$noteId") {
                contentType(ContentType.Application.Json)
                auth(tokens.friend)
                setBody("""{"content":"Edited by project editor"}""")
            }

            assertEquals(HttpStatusCode.OK, response.status)
        }
    }

    @Test
    fun `get all notes returns individual and project notes`() = runTest {
        testApplication {
            val (client, tokens) = setupTestEnvironment()

            client.createNote(tokens.owner, "Individual", "Private content")

            val projectId = client.createProject(tokens.owner, "Multi Notes")
            client.createNote(tokens.owner, "Project Note", "In project", projectId)

            val response = client.get("/notes") { auth(tokens.owner) }

            assertEquals(HttpStatusCode.OK, response.status)
            val body = response.bodyAsText()
            assertTrue(body.contains("Individual"))
            assertTrue(body.contains("Project Note"))
        }
    }

    @Test
    fun `delete note removes it from list`() = runTest {
        testApplication {
            val (client, tokens) = setupTestEnvironment()

            val noteId = client.createNote(tokens.owner, "To Delete", "Content")

            // Удаляем
            val deleteResponse = client.delete("/notes/$noteId") { auth(tokens.owner) }
            assertEquals(HttpStatusCode.NoContent, deleteResponse.status)

            val getResponse = client.get("/notes/$noteId") { auth(tokens.owner) }
            assertEquals(HttpStatusCode.NotFound, getResponse.status)

            val listResponse = client.get("/notes") { auth(tokens.owner) }
            assertFalse(listResponse.bodyAsText().contains("To Delete"))
        }
    }

    @Test
    fun `share note with invalid role - 400`() = runTest {
        testApplication {
            val (client, tokens) = setupTestEnvironment()

            val noteId = client.createNote(tokens.owner, "Test", "Content")

            val response = client.post("/notes/$noteId/share") {
                contentType(ContentType.Application.Json)
                auth(tokens.owner)
                setBody("""{"targetUserId":${tokens.friendId},"role":"INVALID_ROLE"}""")
            }

            assertEquals(HttpStatusCode.BadRequest, response.status)
        }
    }

    @Test
    fun `share note with non-existent user - 404`() = runTest {
        testApplication {
            val (client, tokens) = setupTestEnvironment()

            val noteId = client.createNote(tokens.owner, "Test", "Content")

            val response = client.post("/notes/$noteId/share") {
                contentType(ContentType.Application.Json)
                auth(tokens.owner)
                setBody("""{"targetUserId":99999,"role":"READ_WRITE"}""")
            }

            assertEquals(HttpStatusCode.NotFound, response.status)
        }
    }

    @Test
    fun `complex scenari - project VIEWER + individual READ_WRITE = project wins`() = runTest {
        testApplication {
            val (client, tokens) = setupTestEnvironment()

            val projectId = client.createProject(tokens.owner, "Priority Test")
            client.post("/projects/$projectId/members") {
                contentType(ContentType.Application.Json)
                auth(tokens.owner)
                setBody("""{"email":"friend@test.com","role":"VIEWER"}""")
            }

            val noteId = client.createNote(tokens.owner, "Project Note", "Content", projectId)

            client.post("/notes/$noteId/share") {
                contentType(ContentType.Application.Json)
                auth(tokens.owner)
                setBody("""{"targetUserId":${tokens.friendId},"role":"READ_WRITE"}""")
            }

            val getResponse = client.get("/notes/$noteId") { auth(tokens.friend) }
            assertEquals(HttpStatusCode.OK, getResponse.status)

            val editResponse = client.patch("/notes/$noteId") {
                contentType(ContentType.Application.Json)
                auth(tokens.friend)
                setBody("""{"content":"Try to override"}""")
            }
            assertEquals(HttpStatusCode.Forbidden, editResponse.status)
        }
    }
}