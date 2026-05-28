package com.example.service

import com.example.dto.CreateNoteRequest
import com.example.dto.NoteResponse
import com.example.dto.UpdateNoteRequest
import com.example.model.Note
import com.example.model.NoteAccessRole
import com.example.model.ProjectRole
import com.example.repository.NoteRepository
import com.example.repository.ProjectRepository

class NoteService(
    private val noteRepository: NoteRepository,
    private val projectRepository: ProjectRepository
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

            Result.success(NoteResponse(
                id = note.id.value,
                title = note.title,
                content = note.content,
                projectId = note.project?.id?.value,
                ownerId = note.owner.id.value,
                accessRole = "READ_WRITE"
            ))
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

            Result.success(NoteResponse(
                id = note.id.value,
                title = note.title,
                content = note.content,
                projectId = note.project?.id?.value,
                ownerId = note.owner.id.value,
                accessRole = accessRole
            ))
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
                    id = note.id.value,
                    title = note.title,
                    content = note.content,
                    projectId = note.project?.id?.value,
                    ownerId = note.owner.id.value,
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
            val note = noteRepository.findById(noteId) ?: throw IllegalArgumentException("Note not found")

            if (note.owner.id.value != ownerId) {
                return Result.failure(IllegalArgumentException("Only the owner can share this note"))
            }

            noteRepository.addAccess(noteId, targetUserId, role)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun determineAccessRole(userId: Int, note: Note): String? {
        if (note.owner.id.value == userId) return "READ_WRITE"

        note.project?.let { project ->
            val member = projectRepository.getMember(project.id.value, userId)
            if (member != null) {
                return when (member.getRoleEnum()) {
                    ProjectRole.OWNER, ProjectRole.EDITOR -> "READ_WRITE"
                    ProjectRole.VIEWER -> "READ_ONLY"
                }
            }
        }

        val access = noteRepository.getAccess(note.id.value, userId)
        if (access != null) {
            return when (access.getRoleEnum()) {
                NoteAccessRole.READ_WRITE -> "READ_WRITE"
                NoteAccessRole.READ_ONLY -> "READ_ONLY"
            }
        }

        return null
    }
}