package com.example.teqnotes.features.home.domain.usecase

import com.example.teqnotes.features.home.domain.model.Note
import com.example.teqnotes.features.home.domain.repository.HomeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetNoteByIdUseCase @Inject constructor(
    private val repository: HomeRepository
) {
    operator fun invoke(noteId: String): Flow<Note?> {
        return repository.getNoteById(noteId)
    }
}