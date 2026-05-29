package com.example.shared.utils

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.auth.principal
import io.ktor.server.response.*
import io.ktor.util.reflect.TypeInfo
import kotlinx.serialization.Serializable

fun ApplicationCall.getUserId(): Int {
    val principal = principal<JWTPrincipal>()
        ?: throw IllegalArgumentException("Unauthorized")
    return principal.payload.subject.toInt()
}

suspend fun <T> Result<T>.respond(
    call: ApplicationCall,
    successStatus: HttpStatusCode = HttpStatusCode.OK
) {
    fold(
        onSuccess = { value ->
            if (value is Unit) call.respond(successStatus)
            else call.respond(successStatus, value as Any)
        },
        onFailure = { error ->
            val status = when (error.message) {
                "Forbidden", "Forbidden: Read-only access",
                "Only the owner can share this note",
                "Forbidden: Only owner can update project details",
                "Forbidden: Only owner can delete project",
                "Forbidden: Only owner can add members",
                "This request is not for you" -> HttpStatusCode.Forbidden

                "Invalid role",
                "User not found",
                "Note not found", "Project not found",
                "Request not found", "Relationship not found",
                "User not found by email", "User with this email not found" -> HttpStatusCode.NotFound

                "Invalid credentials", "Unauthorized" -> HttpStatusCode.Unauthorized

                else -> HttpStatusCode.BadRequest
            }
            call.respond(status, mapOf("error" to (error.message ?: "Unknown error")))
        }
    )
}

@Serializable
data class ShareNoteRequest(val targetUserId: Int, val role: String)

@Serializable
data class AddMemberRequest(val email: String, val role: String)