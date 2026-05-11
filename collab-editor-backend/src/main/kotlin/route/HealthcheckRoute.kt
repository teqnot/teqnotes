package com.example.route

import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import kotlinx.serialization.Serializable

@Serializable
data class HealthResponse(
    val status: String,
    val timestamp: Long
)

fun Route.healthcheckRouting() {
    get("/ping") {
        call.respondText("OK", contentType = ContentType.Text.Plain)
    }

    get("/health") {
        call.respond(HttpStatusCode.OK, HealthResponse(status = "ok", timestamp = System.currentTimeMillis()))
    }
}