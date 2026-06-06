package com.example.teqnotes.features.home.data.repository

import com.example.teqnotes.features.home.data.local.NoteDao
import com.example.teqnotes.features.home.data.local.NoteEntity
import com.example.teqnotes.features.home.data.local.ProjectDao
import com.example.teqnotes.features.home.data.local.ProjectEntity
import com.example.teqnotes.features.home.data.remote.CreateNoteRequest
import com.example.teqnotes.features.home.data.remote.CreateProjectRequest
import com.example.teqnotes.features.home.data.remote.NoteApi
import com.example.teqnotes.features.home.data.remote.NoteDto
import com.example.teqnotes.features.home.data.remote.ProjectApi
import com.example.teqnotes.features.home.data.remote.ProjectDto
import com.example.teqnotes.features.home.data.remote.UpdateNoteRequest
import com.example.teqnotes.features.home.data.remote.UpdateProjectRequest
import com.example.teqnotes.features.home.domain.model.Note
import com.example.teqnotes.features.home.domain.model.Project
import com.example.teqnotes.features.home.domain.repository.HomeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlin.collections.emptyList

class HomeRepositoryImpl @Inject constructor(
    private val noteApi: NoteApi,
    private val projectApi: ProjectApi,
    private val noteDao: NoteDao,
    private val projectDao: ProjectDao
) : HomeRepository {

    override fun getIndividualNotes(): Flow<List<Note>> = flow {
        noteApi.getAllNotes()
            .onSuccess { dtos: List<NoteDto> ->
                val entities: List<NoteEntity> = dtos
                    .filter { it.projectId == null }
                    .map { dto: NoteDto -> dto.toEntity() }
                noteDao.insertNotes(entities)
            }
        noteDao.getIndividualNotes().collect { entities: List<NoteEntity> ->
            val notes: List<Note> = entities.map { entity: NoteEntity -> entity.toDomain() }
            emit(notes)
        }
    }.flowOn(Dispatchers.IO)

    override fun getProjects(): Flow<List<Project>> = flow {
        projectApi.getProjects()
            .onSuccess { dtos: List<ProjectDto> ->
                val entities: List<ProjectEntity> = dtos.map { dto: ProjectDto -> dto.toEntity() }
                projectDao.insertProjects(entities)
            }
        projectDao.getProjects().collect { entities: List<ProjectEntity> ->
            val projects: List<Project> = entities.map { entity: ProjectEntity -> entity.toDomain() }
            emit(projects)
        }
    }.flowOn(Dispatchers.IO)

    override fun getNotesByProject(projectId: String): Flow<List<Note>> = flow {
        val projectIdInt: Int = projectId.toIntOrNull() ?: return@flow emit(emptyList())

        noteApi.getAllNotes()
            .onSuccess { dtos: List<NoteDto> ->
                val entities: List<NoteEntity> = dtos
                    .filter { it.projectId == projectIdInt }
                    .map { dto: NoteDto -> dto.toEntity() }
                noteDao.insertNotes(entities)
            }
        noteDao.getNotesByProject(projectId).collect { entities: List<NoteEntity> ->
            val notes: List<Note> = entities.map { entity: NoteEntity -> entity.toDomain() }
            emit(notes)
        }
    }.flowOn(Dispatchers.IO)

    override fun getNoteById(noteId: String): Flow<Note?> = flow {
        val noteIdInt: Int = noteId.toIntOrNull() ?: return@flow emit(null)

        noteDao.getNoteById(noteId).collect { entity: NoteEntity? ->
            entity?.let { nonNullEntity ->
                emit(nonNullEntity.toDomain())
            } ?: emit(null)
        }

        noteApi.getNote(noteIdInt)
            .onSuccess { dto: NoteDto ->
                val entity = dto.toEntity()
                noteDao.insertNote(entity)
                emit(entity.toDomain())
            }
    }.flowOn(Dispatchers.IO)

    override suspend fun createNote(note: Note) {
        val request = CreateNoteRequest(
            title = note.title,
            content = note.content.takeIf { it.isNotBlank() },
            projectId = note.projectId?.toIntOrNull()
        )
        noteApi.createNote(request)
            .onSuccess { dto: NoteDto -> noteDao.insertNote(dto.toEntity()) }
            .onFailure { throw it }
    }

    override suspend fun createProject(project: Project) {
        val request = CreateProjectRequest(
            name = project.name,
            description = project.description.takeIf { it.isNotBlank() }
        )
        projectApi.createProject(request)
            .onSuccess { dto: ProjectDto -> projectDao.insertProject(dto.toEntity()) }
            .onFailure { throw it }
    }

    override suspend fun updateNote(note: Note) {
        val noteIdInt: Int = note.id.toIntOrNull() ?: throw IllegalArgumentException("Invalid note ID")
        val request = UpdateNoteRequest(
            title = note.title.takeIf { it.isNotBlank() },
            content = note.content.takeIf { it.isNotBlank() }
        )
        noteApi.updateNote(noteIdInt, request)
            .onSuccess { dto: NoteDto -> noteDao.insertNote(dto.toEntity()) }
            .onFailure { throw it }
    }

    override suspend fun updateProject(project: Project) {
        val projectIdInt: Int = project.id.toIntOrNull() ?: throw IllegalArgumentException("Invalid project ID")
        val request = UpdateProjectRequest(
            name = project.name.takeIf { it.isNotBlank() },
            description = project.description.takeIf { it.isNotBlank() }
        )
        projectApi.updateProject(projectIdInt, request)
            .onSuccess { dto: ProjectDto -> projectDao.insertProject(dto.toEntity()) }
            .onFailure { throw it }
    }

    override suspend fun deleteNote(noteId: String) {
        val noteIdInt: Int = noteId.toIntOrNull() ?: throw IllegalArgumentException("Invalid note ID")
        noteApi.deleteNote(noteIdInt)
            .onSuccess { noteDao.deleteNoteById(noteId) }
            .onFailure { throw it }
    }
}

internal fun NoteDto.toEntity(): NoteEntity = NoteEntity(
    id = this.id.toString(),
    title = this.title,
    content = this.content ?: "",
    projectId = this.projectId?.toString(),
    timestamp = System.currentTimeMillis(),
    isArchived = false
)

internal fun NoteEntity.toDomain(): Note = Note(
    id = this.id,
    title = this.title,
    content = this.content,
    projectId = this.projectId,
    timestamp = this.timestamp,
    isArchived = this.isArchived
)

internal fun ProjectDto.toEntity(): ProjectEntity = ProjectEntity(
    id = this.id.toString(),
    name = this.name,
    description = this.description ?: "",
    createdAt = System.currentTimeMillis(),
    isArchived = false
)

internal fun ProjectEntity.toDomain(): Project = Project(
    id = this.id,
    name = this.name,
    description = this.description,
    createdAt = this.createdAt,
    isArchived = this.isArchived
)