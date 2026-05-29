package com.example.features.auth.presentation.route

import com.example.features.auth.domain.service.AuthService
import com.example.features.auth.domain.service.TokenService
import com.example.features.auth.presentation.dto.AuthResponse
import com.example.features.auth.presentation.dto.LoginRequest
import com.example.features.auth.presentation.dto.RefreshRequest
import com.example.features.auth.presentation.dto.RegisterRequest
import com.example.features.auth.presentation.dto.UserInfo
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import kotlinx.serialization.Serializable

fun Route.authRouting(
    authService: AuthService,
    tokenService: TokenService
) {
    route("/auth") {

        /**
         * Register a new user
         *
         * POST /auth/register
         * Body: {"email": "...", "password": "...", "name": "..."}
         * Response: 201 Created + tokens
         */
        post("/register") {
            try {
                val request = call.receive<RegisterRequest>()
                val result = authService.register(request.email, request.password, request.name)

                result.onSuccess { (user, tokens) ->
                    val (accessToken, refreshToken) = tokens
                    call.respond(HttpStatusCode.Created, AuthResponse(
                        accessToken = accessToken,
                        refreshToken = refreshToken,
                        user = UserInfo(
                            id = user.id,       // ✅ UserData.id уже Int
                            email = user.email,
                            name = user.name
                        )
                    ))
                }.onFailure { error ->
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to (error.message ?: "Registration failed")))
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to (e.message ?: "Invalid request")))
            }
        }

        /**
         * Authenticate user
         *
         * POST /auth/login
         * Body: {"email": "...", "password": "..."}
         * Response: 200 OK + tokens
         */
        post("/login") {
            try {
                val request = call.receive<LoginRequest>()
                val result = authService.authenticate(request.email, request.password)

                result.onSuccess { (user, tokens) ->
                    val (accessToken, refreshToken) = tokens
                    call.respond(HttpStatusCode.OK, AuthResponse(
                        accessToken = accessToken,
                        refreshToken = refreshToken,
                        user = UserInfo(
                            id = user.id,
                            email = user.email,
                            name = user.name
                        )
                    ))
                }.onFailure { error ->
                    call.respond(HttpStatusCode.Unauthorized, mapOf("error" to (error.message ?: "Invalid credentials")))
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to (e.message ?: "Invalid request")))
            }
        }

        /**
         * Refresh the tokens
         *
         * POST /auth/refresh
         * Body: {"refreshToken": "..."}
         * Response: 200 OK + new tokens
         */
        post("/refresh") {
            try {
                val request = call.receive<RefreshRequest>()

                val result = tokenService.refreshAccessToken(request.refreshToken)

                result.onSuccess { (user, tokens) ->
                    val (accessToken, refreshToken) = tokens

                    call.respond(HttpStatusCode.OK, AuthResponse(
                        accessToken = accessToken,
                        refreshToken = refreshToken,
                        user = UserInfo(
                            id = user.id,
                            email = user.email,
                            name = user.name
                        )
                    ))
                }.onFailure { error ->
                    call.respond(HttpStatusCode.Unauthorized, mapOf("error" to (error.message ?: "Invalid refresh token")))
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to (e.message ?: "Invalid request")))
            }
        }
    }
}