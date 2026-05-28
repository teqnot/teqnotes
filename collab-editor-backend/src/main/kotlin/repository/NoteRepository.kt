package com.example.repository

import com.example.model.Note
import com.example.model.NoteAccess
import com.example.model.NoteAccesses
import com.example.model.Notes
import com.example.model.Project
import com.example.model.ProjectMember
import com.example.model.ProjectMembers
import com.example.model.User
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction

interface NoteRepository {
    fun create(ownerId: Int, title: String, content: String?, projectId: Int?): Note
    fun findById(id: Int): Note?
    fun findAllByUser(userId: Int): List<Note> // Индивидуальные заметки + заметки из проектов, где пользователь участник
    fun update(id: Int, title: String?, content: String?)
    fun delete(id: Int)
    fun addAccess(noteId: Int, userId: Int, role: String): NoteAccess
    fun getAccess(noteId: Int, userId: Int): NoteAccess?
}

object NoteRepositoryImpl : NoteRepository {
    override fun create(ownerId: Int, title: String, content: String?, projectId: Int?): Note = transaction {
        val owner = User[ownerId]
        val proj = projectId?.let { Project[it] }

        Note.new {
            this.title = title
            this.content = content
            this.owner = owner
            this.project = proj
        }
    }

    override fun findById(id: Int): Note? = transaction {
        Note.findById(id)
    }

    override fun findAllByUser(userId: Int): List<Note> = transaction {
        val ownedNotes = Note.find { Notes.ownerId eq userId }.toList()

        val accessedNoteIds = NoteAccess.find { NoteAccesses.userId eq userId }
            .map { it.note.id.value }

        val accessedNotes = if (accessedNoteIds.isNotEmpty()) {
            Note.find { Notes.id inList accessedNoteIds }.toList()
        } else emptyList()

        val projectIds = ProjectMember.find { ProjectMembers.userId eq userId }
            .map { it.project.id.value }

        val projectNotes = if (projectIds.isNotEmpty()) {
            Note.find { (Notes.projectId inList projectIds) and (Notes.ownerId neq userId) }.toList()
        } else emptyList()

        (ownedNotes + accessedNotes + projectNotes).distinctBy { it.id.value }
    }

    override fun update(id: Int, title: String?, content: String?): Unit = transaction {
        val note = Note.findById(id) ?: throw IllegalArgumentException("Note not found")
        title?.let { note.title = it }
        content?.let { note.content = it }
    }

    override fun delete(id: Int): Unit = transaction {
        Note.findById(id)?.delete()
    }

    override fun addAccess(noteId: Int, userId: Int, role: String) = transaction {
        NoteAccess.new {
            this.note = Note[noteId]
            this.user = User[userId]
            this.role = role
        }
    }

    override fun getAccess(noteId: Int, userId: Int): NoteAccess? = transaction {
        NoteAccess.find {
            (NoteAccesses.noteId eq noteId) and (NoteAccesses.userId eq userId)
        }.firstOrNull()
    }
}