package com.example.features.notes.data.repository


import com.example.features.auth.data.model.User
import com.example.features.notes.data.local.Note
import com.example.features.notes.data.local.NoteAccess
import com.example.features.notes.data.local.NoteAccesses
import com.example.features.notes.data.local.Notes
import com.example.features.notes.data.local.Project
import com.example.features.notes.data.local.ProjectMember
import com.example.features.notes.data.local.ProjectMembers
import com.example.features.notes.data.local.Projects
import com.example.features.notes.domain.model.NoteAccessData
import com.example.features.notes.domain.model.NoteData
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction

interface NoteRepository {
    fun create(ownerId: Int, title: String, content: String?, projectId: Int?): NoteData
    fun findById(id: Int): NoteData?
    fun findAllByUser(userId: Int): List<NoteData>
    fun update(id: Int, title: String?, content: String?)
    fun delete(id: Int)
    fun addAccess(noteId: Int, userId: Int, role: String): NoteAccessData
    fun getAccess(noteId: Int, userId: Int): NoteAccessData?
}

object NoteRepositoryImpl : NoteRepository {

    override fun create(ownerId: Int, title: String, content: String?, projectId: Int?): NoteData = transaction {
        val note = Note.new {
            this.title = title
            this.content = content
            this.owner = User[ownerId]
            this.project = projectId?.let { Project[it] }
        }

        NoteData(
            id = note.id.value,
            title = note.title,
            content = note.content,
            ownerId = note.owner.id.value,
            projectId = note.project?.id?.value,
            createdAt = note.createdAt,
            updatedAt = note.updatedAt
        )
    }

    override fun findById(id: Int): NoteData? = transaction {
        val note = Note.findById(id) ?: return@transaction null
        NoteData(
            id = note.id.value,
            title = note.title,
            content = note.content,
            ownerId = note.owner.id.value,
            projectId = note.project?.id?.value,
            createdAt = note.createdAt,
            updatedAt = note.updatedAt
        )
    }

    override fun findAllByUser(userId: Int): List<NoteData> = transaction {
        val ownedNotes = Note.find { Notes.ownerId eq userId }.map { toNoteData(it) }

        val accessedNoteIds = NoteAccesses.select(NoteAccesses.noteId)
            .where { NoteAccesses.userId eq userId }
            .map {
                val id = it[NoteAccesses.noteId].value
                id
            }

        val accessedNotes = if (accessedNoteIds.isNotEmpty()) {
            val entityIds = accessedNoteIds.map { EntityID(it, Notes) }
            Note.find { Notes.id inList entityIds }.map {
                val data = toNoteData(it)
                data
            }
        } else {
            emptyList()
        }

        val projectIds = ProjectMembers.select(ProjectMembers.projectId)
            .where { ProjectMembers.userId eq userId }
            .map { it[ProjectMembers.projectId].value }
        println("[NoteRepo] projectIds: $projectIds")

        val projectNotes = if (projectIds.isNotEmpty()) {
            val projEntityIds = projectIds.map { EntityID(it, Projects) }
            Note.find { (Notes.projectId inList projEntityIds) and (Notes.ownerId neq userId) }
                .map { toNoteData(it) }
        } else emptyList()

        val result = (ownedNotes + accessedNotes + projectNotes).distinctBy { it.id }
        println("[NoteRepo] findAllByUser(userId=$userId) END: returning ${result.size} notes")
        result
    }

    override fun update(id: Int, title: String?, content: String?): Unit = transaction {
        val note = Note.findById(id) ?: throw IllegalArgumentException("Note not found")
        title?.let { note.title = it }
        content?.let { note.content = it }
    }

    override fun delete(id: Int): Unit = transaction {
        Note.findById(id)?.delete()
    }

    override fun addAccess(noteId: Int, userId: Int, role: String): NoteAccessData = transaction {
        val access = NoteAccess.new {
            this.note = Note[noteId]
            this.user = User[userId]
            this.role = role
        }

        NoteAccessData(
            noteId = access.note.id.value,
            userId = access.user.id.value,
            role = access.role,
            grantedBy = access.grantedBy?.id?.value,
            grantedAt = access.grantedAt
        )
    }

    override fun getAccess(noteId: Int, userId: Int): NoteAccessData? = transaction {
        val access = NoteAccess.find {
            (NoteAccesses.noteId eq noteId) and (NoteAccesses.userId eq userId)
        }.firstOrNull() ?: return@transaction null

        NoteAccessData(
            noteId = access.note.id.value,
            userId = access.user.id.value,
            role = access.role,
            grantedBy = access.grantedBy?.id?.value,
            grantedAt = access.grantedAt
        )
    }

    private fun toNoteData(note: Note): NoteData = NoteData(
        id = note.id.value,
        title = note.title,
        content = note.content,
        ownerId = note.owner.id.value,
        projectId = note.project?.id?.value,
        createdAt = note.createdAt,
        updatedAt = note.updatedAt
    )
}