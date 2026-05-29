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

class AuthRouteTest : IntegrationTestBase() {

    @Test
    fun `register - success`() = runTest {
        testApplication {
            val (client, tokens) = setupTestEnvironment()

            val response = client.post("/auth/register") {
                contentType(ContentType.Application.Json)
                setBody("""{"email":"new@test.com","password":"pass123","name":"New User"}""")
            }

            assertEquals(HttpStatusCode.Created, response.status)
            val json = Json.parseToJsonElement(response.bodyAsText()).jsonObject
            assertTrue(json["accessToken"]?.jsonPrimitive?.content?.isNotEmpty() ?: false)
            assertTrue(json["refreshToken"]?.jsonPrimitive?.content?.isNotEmpty() ?: false)
        }
    }

    @Test
    fun `register - duplicate email`() = runTest {
        testApplication {
            val (client, _) = setupTestEnvironment()

            client.post("/auth/register") {
                contentType(ContentType.Application.Json)
                setBody("""{"email":"dup@test.com","password":"pass","name":"Dup"}""")
            }

            val response = client.post("/auth/register") {
                contentType(ContentType.Application.Json)
                setBody("""{"email":"dup@test.com","password":"pass","name":"Dup2"}""")
            }

            assertEquals(HttpStatusCode.BadRequest, response.status)
            assertTrue(response.bodyAsText().contains("already exists"))
        }
    }

    @Test
    fun `login - success`() = runTest {
        testApplication {
            val (client, _) = setupTestEnvironment()

            val response = client.post("/auth/login") {
                contentType(ContentType.Application.Json)
                setBody("""{"email":"owner@test.com","password":"password123"}""")
            }

            assertEquals(HttpStatusCode.OK, response.status)
            val json = Json.parseToJsonElement(response.bodyAsText()).jsonObject
            assertTrue(json["accessToken"]?.jsonPrimitive?.content?.isNotEmpty() ?: false)
        }
    }

    @Test
    fun `login - wrong password`() = runTest {
        testApplication {
            val (client, _) = setupTestEnvironment()

            val response = client.post("/auth/login") {
                contentType(ContentType.Application.Json)
                setBody("""{"email":"owner@test.com","password":"wrongpass"}""")
            }

            assertEquals(HttpStatusCode.Unauthorized, response.status)
            assertTrue(response.bodyAsText().contains("Invalid credentials"))
        }
    }

    @Test
    fun `refresh - success`() = runTest {
        testApplication {
            val (client, _) = setupTestEnvironment()

            val loginResponse = client.post("/auth/login") {
                contentType(ContentType.Application.Json)
                setBody("""{"email":"owner@test.com","password":"password123"}""")
            }
            val refreshToken = Json.parseToJsonElement(loginResponse.bodyAsText()).jsonObject["refreshToken"]?.jsonPrimitive?.content ?: ""

            val response = client.post("/auth/refresh") {
                contentType(ContentType.Application.Json)
                setBody("""{"refreshToken":"$refreshToken"}""")
            }

            assertEquals(HttpStatusCode.OK, response.status)
            val json = Json.parseToJsonElement(response.bodyAsText()).jsonObject
            assertTrue(json["accessToken"]?.jsonPrimitive?.content?.isNotEmpty() ?: false)
        }
    }

    @Test
    fun `refresh - invalid token`() = runTest {
        testApplication {
            val (client, _) = setupTestEnvironment()

            val response = client.post("/auth/refresh") {
                contentType(ContentType.Application.Json)
                setBody("""{"refreshToken":"invalid-token"}""")
            }

            assertEquals(HttpStatusCode.Unauthorized, response.status)
        }
    }
}