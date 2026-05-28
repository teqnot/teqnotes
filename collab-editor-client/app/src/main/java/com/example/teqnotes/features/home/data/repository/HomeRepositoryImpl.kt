package com.example.teqnotes.features.home.data.repository

import com.example.teqnotes.features.home.data.local.NoteDao
import com.example.teqnotes.features.home.data.local.NoteEntity
import com.example.teqnotes.features.home.data.local.ProjectDao
import com.example.teqnotes.features.home.data.local.ProjectEntity
import com.example.teqnotes.features.home.domain.model.Note
import com.example.teqnotes.features.home.domain.model.Project
import com.example.teqnotes.features.home.domain.repository.HomeRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class HomeRepositoryImpl @Inject constructor(
    private val noteDao: NoteDao,
    private val projectDao: ProjectDao
) : HomeRepository {

    override fun getIndividualNotes(): Flow<List<Note>> {
        return noteDao.getIndividualNotes().map { entities ->
            entities.map { entity ->
                Note(
                    id = entity.id,
                    title = entity.title,
                    content = entity.content,
                    projectId = entity.projectId,
                    timestamp = entity.timestamp,
                    isArchived = entity.isArchived
                )
            }
        }
    }

    override fun getProjects(): Flow<List<Project>> {
        return projectDao.getProjects().map { entities ->
            entities.map { entity ->
                Project(
                    id = entity.id,
                    name = entity.name,
                    description = entity.description,
                    createdAt = entity.createdAt,
                    isArchived = entity.isArchived
                )
            }
        }
    }

    override fun getNotesByProject(projectId: String): Flow<List<Note>> {
        return noteDao.getNotesByProject(projectId).map { entities ->
            entities.map { entity ->
                Note(
                    id = entity.id,
                    title = entity.title,
                    content = entity.content,
                    projectId = entity.projectId,
                    timestamp = entity.timestamp,
                    isArchived = entity.isArchived
                )
            }
        }
    }

    override fun getNoteById(noteId: String): Flow<Note?> {
        return noteDao.getNoteById(noteId).map { entity ->
            entity?.let {
                Note(
                    id = it.id,
                    title = it.title,
                    content = it.content,
                    projectId = it.projectId,
                    timestamp = it.timestamp,
                    isArchived = it.isArchived
                )
            }
        }
    }

    override suspend fun createNote(note: Note) {
        noteDao.insertNote(
            NoteEntity(
                id = note.id,
                title = note.title,
                content = note.content,
                projectId = note.projectId,
                timestamp = note.timestamp,
                isArchived = note.isArchived
            )
        )
    }

    override suspend fun createProject(project: Project) {
        projectDao.insertProject(
            ProjectEntity(
                id = project.id,
                name = project.name,
                description = project.description,
                createdAt = project.createdAt,
                isArchived = project.isArchived
            )
        )
    }

    override suspend fun updateNote(note: Note) {
        noteDao.updateNote(
            NoteEntity(
                id = note.id,
                title = note.title,
                content = note.content,
                projectId = note.projectId,
                timestamp = note.timestamp,
                isArchived = note.isArchived
            )
        )
    }

    override suspend fun updateProject(project: Project) {
        projectDao.updateProject(
            ProjectEntity(
                id = project.id,
                name = project.name,
                description = project.description,
                createdAt = project.createdAt,
                isArchived = project.isArchived
            )
        )
    }

    override suspend fun archiveNote(noteId: String) {
        noteDao.archiveNote(noteId)
    }

    override suspend fun archiveProject(projectId: String) {
        projectDao.archiveProject(projectId)
    }

    override suspend fun deleteNote(noteId: String) {
        noteDao.deleteNoteById(noteId)
    }
}