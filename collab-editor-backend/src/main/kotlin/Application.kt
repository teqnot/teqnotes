package com.example

import com.example.config.DatabaseConfig
import com.example.config.SecurityConfig
import com.example.repository.RefreshTokenRepositoryImpl
import com.example.repository.UserRepositoryImpl
import com.example.route.authRouting
import com.example.service.AuthService
import com.example.service.TokenService
import io.ktor.server.application.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.routing.routing

fun main(args: Array<String>) {
    DatabaseConfig.init()

    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        configureSerialization()
        configureSecurity()
        configureRouting()
    }.start(wait = true)
}

fun Application.configureSecurity() {
    SecurityConfig.configureJWT(this)
}

fun Application.configureRouting() {
    val refreshTokenRepository = RefreshTokenRepositoryImpl
    val userRepository = UserRepositoryImpl
    val tokenService = TokenService(refreshTokenRepository, userRepository)
    val authService = AuthService(userRepository, tokenService)

    routing {
        authRouting(authService, tokenService)
    }
}
