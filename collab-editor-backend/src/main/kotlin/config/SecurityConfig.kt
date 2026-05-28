package com.example.config

import io.ktor.server.application.*
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import java.time.Duration

object SecurityConfig {
    val SECRET_KEY = System.getenv("JWT_SECRET")
        ?: "super-super-secret-key-change-in-production"
    val ISSUER = System.getenv("JWT_ISSUER")
        ?: "collab-editor"
    val AUDIENCE = System.getenv("JWT_AUDIENCE")
        ?: "users"
    val EXPIRES_IN = Duration.ofHours(24)

    val algorithm: Algorithm = Algorithm.HMAC256(SECRET_KEY)

    fun configureJWT(application: Application) {
        application.install(Authentication) {
            jwt("auth-jwt") {
                realm = "collab-editor"
                verifier(
                    JWT
                        .require(algorithm)
                        .withAudience(AUDIENCE)
                        .withIssuer(ISSUER)
                        .build()
                )
                validate { credential ->
                    if (credential.payload.audience.contains(AUDIENCE)) {
                        JWTPrincipal(credential.payload)
                    } else null
                }
            }
        }
    }
}
