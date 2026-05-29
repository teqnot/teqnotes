package com.example.features.notes.domain.service

import com.example.features.auth.data.repository.UserRepository
import com.example.features.notes.presentation.dto.CreateNoteRequest
import com.example.features.notes.presentation.dto.NoteResponse
import com.example.features.notes.presentation.dto.UpdateNoteRequest
import com.example.features.notes.data.local.Note
import com.example.features.notes.data.repository.NoteRepository
import com.example.features.notes.data.repository.ProjectRepository
import com.example.features.notes.domain.model.enums.NoteAccessRole
import com.example.features.notes.data.local.ProjectRole
import com.example.features.notes.domain.model.NoteData

class NoteService(
    private val noteRepository: NoteRepository,
    private val projectRepository: ProjectRepository,
    private val userRepository: UserRepository
) {

    fun createNote(userId: Int, request: CreateNoteRequest): Result<NoteResponse> {
        return try {
            if (request.projectId != null) {
                val member = projectRepository.getMember(request.projectId, userId)
                if (member == null || member.role !in listOf("OWNER", "EDITOR")) {
                    return Result.failure(IllegalArgumentException("No permission to create notes in this project"))
                }
            }

            val note = noteRepository.create(
                ownerId = userId,
                title = request.title,
                content = request.content,
                projectId = request.projectId
            )

            Result.success(
                NoteResponse(
                    id = note.id,
                    title = note.title,
                    content = note.content,
                    projectId = note.projectId,
                    ownerId = note.ownerId,
                    accessRole = "READ_WRITE"
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getNote(userId: Int, noteId: Int): Result<NoteResponse> {
        return try {
            val note = noteRepository.findById(noteId) ?: throw IllegalArgumentException("Note not found")

            val accessRole = determineAccessRole(userId, note)

            if (accessRole == null) {
                return Result.failure(IllegalArgumentException("Forbidden"))
            }

            Result.success(
                NoteResponse(
                    id = note.id,
                    title = note.title,
                    content = note.content,
                    projectId = note.projectId,
                    ownerId = note.ownerId,
                    accessRole = accessRole
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun updateNote(userId: Int, noteId: Int, request: UpdateNoteRequest): Result<NoteResponse> {
        return try {
            val note = noteRepository.findById(noteId) ?: throw IllegalArgumentException("Note not found")
            val accessRole = determineAccessRole(userId, note)

            if (accessRole != "READ_WRITE") {
                return Result.failure(IllegalArgumentException("Forbidden: Read-only access"))
            }

            noteRepository.update(noteId, request.title, request.content)

            getNote(userId, noteId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun deleteNote(userId: Int, noteId: Int): Result<Unit> {
        return try {
            val note = noteRepository.findById(noteId) ?: throw IllegalArgumentException("Note not found")
            val accessRole = determineAccessRole(userId, note)

            if (accessRole != "READ_WRITE") {
                return Result.failure(IllegalArgumentException("Forbidden"))
            }

            noteRepository.delete(noteId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getAllNotes(userId: Int): Result<List<NoteResponse>> {
        return try {
            val notes = noteRepository.findAllByUser(userId)
            val responses = notes.map { note ->
                val role = determineAccessRole(userId, note) ?: "READ_ONLY"
                NoteResponse(
                    id = note.id,
                    title = note.title,
                    content = note.content,
                    projectId = note.projectId,
                    ownerId = note.ownerId,
                    accessRole = role
                )
            }
            Result.success(responses)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun grantAccessToUser(ownerId: Int, noteId: Int, targetUserId: Int, role: String): Result<Unit> {
        return try {
            if (role !in listOf("READ_ONLY", "READ_WRITE")) {
                return Result.failure(IllegalArgumentException("Invalid role: $role"))
            }

            val note = noteRepository.findById(noteId) ?: throw IllegalArgumentException("Note not found")

            if (note.ownerId != ownerId) {
                return Result.failure(IllegalArgumentException("Only the owner can share this note"))
            }

            userRepository.findById(targetUserId)
                ?: return Result.failure(IllegalArgumentException("User not found"))

            noteRepository.addAccess(noteId, targetUserId, role)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun determineAccessRole(userId: Int, note: NoteData): String? {
        if (note.ownerId == userId) return "READ_WRITE"

        note.projectId?.let { projectId ->
            val member = projectRepository.getMember(projectId, userId)
            if (member != null) {
                return when (member.role) {
                    "OWNER", "EDITOR" -> "READ_WRITE"
                    "VIEWER" -> "READ_ONLY"
                    else -> null
                }
            }
        }

        val access = noteRepository.getAccess(note.id, userId)
        if (access != null) {
            return when (access.role) {
                "READ_WRITE" -> "READ_WRITE"
                "READ_ONLY" -> "READ_ONLY"
                else -> null
            }
        }

        return null
    }
}