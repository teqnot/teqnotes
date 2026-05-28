package com.example.route

import com.example.dto.CreateNoteRequest
import com.example.dto.UpdateNoteRequest
import com.example.service.NoteService
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.patch
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.noteRouting(noteService: NoteService) {
    authenticate("auth-jwt") {
        route("/notes") {

            // GET /notes - Список всех доступных заметок
            get {
                call.getUserId().let { userId ->
                    noteService.getAllNotes(userId).respond(call)
                }
            }

            // POST /notes - Создать заметку
            post {
                val userId = call.getUserId()
                val request = call.receive<CreateNoteRequest>()
                noteService.createNote(userId, request).respond(call, HttpStatusCode.Created)
            }

            // GET /notes/{id} - Получить заметку
            get("{id}") {
                val userId = call.getUserId()
                val noteId = call.parameters["id"]?.toIntOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid note ID"))

                noteService.getNote(userId, noteId).respond(call)
            }

            // PATCH /notes/{id} - Обновить заметку
            patch("{id}") {
                val userId = call.getUserId()
                val noteId = call.parameters["id"]?.toIntOrNull()
                    ?: return@patch call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid note ID"))

                val request = call.receive<UpdateNoteRequest>()
                noteService.updateNote(userId, noteId, request).respond(call)
            }

            // DELETE /notes/{id} - Удалить заметку
            delete("{id}") {
                val userId = call.getUserId()
                val noteId = call.parameters["id"]?.toIntOrNull()
                    ?: return@delete call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid note ID"))

                noteService.deleteNote(userId, noteId).respond(call, HttpStatusCode.NoContent)
            }

            // POST /notes/{id}/share - Предоставить доступ другу
            post("{id}/share") {
                val userId = call.getUserId()
                val noteId = call.parameters["id"]?.toIntOrNull()
                    ?: return@post call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid note ID"))

                val request = call.receive<ShareNoteRequest>()
                noteService.grantAccessToUser(userId, noteId, request.targetUserId, request.role)
                    .respond(call, HttpStatusCode.OK)
            }
        }
    }
}