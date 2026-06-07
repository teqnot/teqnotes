package com.example.features.auth.presentation.route

import com.example.features.auth.domain.service.UserService
import com.example.shared.utils.getUserId
import com.example.shared.utils.respond
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.route

fun Route.userRouting(userService: UserService) {
    authenticate("auth-jwt") {
        route("/users") {
            // GET /users/search?query=...
            get("/search") {
                val currentUserId = call.getUserId()
                val query = call.request.queryParameters["query"] ?: ""

                if (query.isBlank()) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Query parameter 'query' is required"))
                    return@get
                }

                userService.searchUsers(currentUserId, query).respond(call)
            }

            // DELETE /users/me
            delete("/me") {
                val userId = call.getUserId()
                userService.deleteAccount(userId).respond(call, HttpStatusCode.NoContent)
            }
        }
    }
}