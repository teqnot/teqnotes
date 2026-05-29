package com.example

import com.example.config.DatabaseConfig
import com.example.config.SecurityConfig
import com.example.features.friends.data.repository.FriendshipRepositoryImpl
import com.example.features.notes.data.repository.NoteRepositoryImpl
import com.example.features.notes.data.repository.ProjectRepositoryImpl
import com.example.features.auth.data.repository.RefreshTokenRepositoryImpl
import com.example.features.auth.data.repository.UserRepositoryImpl
import com.example.features.auth.presentation.route.authRouting
import com.example.features.friends.presentation.route.friendRouting
import com.example.shared.utils.healthcheckRouting
import com.example.features.notes.presentation.route.noteRouting
import com.example.features.notes.presentation.route.projectRouting
import com.example.features.auth.domain.service.AuthService
import com.example.features.friends.domain.service.FriendshipService
import com.example.features.notes.domain.service.NoteService
import com.example.features.notes.domain.service.ProjectService
import com.example.features.auth.domain.service.TokenService
import io.ktor.server.application.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.routing.routing
import io.ktor.serialization.kotlinx.json.json

fun main(args: Array<String>) {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        module()
    }.start(wait = true)
}

fun Application.module() {
    install(ContentNegotiation) {
        json()
    }

    DatabaseConfig.init()

    configureSecurity()

    val userRepo = UserRepositoryImpl
    val refreshRepo = RefreshTokenRepositoryImpl
    val projectRepo = ProjectRepositoryImpl
    val noteRepo = NoteRepositoryImpl
    val friendRepo = FriendshipRepositoryImpl

    val tokenService = TokenService(refreshRepo, userRepo)
    val authService = AuthService(userRepo, tokenService)

    val noteService = NoteService(noteRepo, projectRepo, userRepo)
    val projectService = ProjectService(projectRepo, userRepo)
    val friendService = FriendshipService(friendRepo, userRepo)

    configureRouting(
        authService = authService,
        tokenService = tokenService,
        noteService = noteService,
        projectService = projectService,
        friendService = friendService
    )
}

fun Application.configureSecurity() {
    SecurityConfig.configureJWT(this)
}

fun Application.configureRouting(
    authService: AuthService,
    tokenService: TokenService,
    noteService: NoteService,
    projectService: ProjectService,
    friendService: FriendshipService
) {
    routing {
        healthcheckRouting()
        authRouting(authService, tokenService)

        noteRouting(noteService)
        projectRouting(projectService)
        friendRouting(friendService)
    }
}
