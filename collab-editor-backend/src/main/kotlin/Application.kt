package com.example

import com.example.config.DatabaseConfig
import com.example.config.SecurityConfig
import com.example.repository.FriendshipRepositoryImpl
import com.example.repository.NoteRepositoryImpl
import com.example.repository.ProjectRepositoryImpl
import com.example.repository.RefreshTokenRepositoryImpl
import com.example.repository.UserRepositoryImpl
import com.example.route.authRouting
import com.example.route.friendRouting
import com.example.route.healthcheckRouting
import com.example.route.noteRouting
import com.example.route.projectRouting
import com.example.service.AuthService
import com.example.service.FriendshipService
import com.example.service.NoteService
import com.example.service.ProjectService
import com.example.service.TokenService
import io.ktor.server.application.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.routing.routing
import io.ktor.serialization.kotlinx.json.json

fun main(args: Array<String>) {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
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

        val noteService = NoteService(noteRepo, projectRepo)
        val projectService = ProjectService(projectRepo, userRepo)
        val friendService = FriendshipService(friendRepo, userRepo)

        configureRouting(
            authService = authService,
            tokenService = tokenService,
            noteService = noteService,
            projectService = projectService,
            friendService = friendService
        )
    }.start(wait = true)
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
