package com.example.teqnotes.features.home.domain.repository

import com.example.teqnotes.features.home.domain.model.Note
import com.example.teqnotes.features.home.domain.model.Project
import kotlinx.coroutines.flow.Flow

interface HomeRepository {

    fun getIndividualNotes(): Flow<List<Note>>
    fun getProjects(): Flow<List<Project>>
    fun getNotesByProject(projectId: String): Flow<List<Note>>
    fun getNoteById(noteId: String): Flow<Note?>

    suspend fun createNote(note: Note)
    suspend fun createProject(project: Project)
    suspend fun updateNote(note: Note)
    suspend fun updateProject(project: Project)
    suspend fun archiveNote(noteId: String)
    suspend fun archiveProject(projectId: String)
}