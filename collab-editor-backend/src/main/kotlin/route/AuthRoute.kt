package com.example.route

import com.example.model.User
import com.example.model.Users
import com.example.service.AuthService
import com.example.service.TokenService
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route

data class RegisterRequest(
    val email: String,
    val password: String,
    val name: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class RefreshRequest(
    val refreshToken: String
)

data class AuthResponse(
    val accessToken: String,
    val refreshToken: String,
    val user: UserInfo
)

data class UserInfo(
    val id: Int,
    val email: String,
    val name: String
)

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

                val result = authService.register(
                    request.email,
                    request.password,
                    request.name
                )

                result.onSuccess { (user, tokens) ->
                    val (accessToken, refreshToken) = tokens

                    call.respond(HttpStatusCode.Created, AuthResponse(
                        accessToken = accessToken,
                        refreshToken = refreshToken,
                        user = UserInfo(
                            id = user.id.value,
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

                val result = authService.authenticate(
                    request.email,
                    request.password
                )

                result.onSuccess { (accessToken, refreshToken) ->
                    val user = User.find { Users.email eq request.email }.first()

                    call.respond(HttpStatusCode.OK, AuthResponse(
                        accessToken = accessToken,
                        refreshToken = refreshToken,
                        user = UserInfo(
                            id = user.id.value,
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

                result.onSuccess { (accessToken, refreshToken) ->
                    call.respond(HttpStatusCode.OK, AuthResponse(
                        accessToken = accessToken,
                        refreshToken = refreshToken,
                        user = UserInfo(
                            id = 0,
                            email = "",
                            name = ""
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