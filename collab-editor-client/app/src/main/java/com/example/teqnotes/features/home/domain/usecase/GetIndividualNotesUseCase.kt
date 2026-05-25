package com.example.teqnotes.features.home.domain.usecase

import com.example.teqnotes.features.home.domain.model.Note
import com.example.teqnotes.features.home.domain.repository.HomeRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetIndividualNotesUseCase @Inject constructor(
    private val repository: HomeRepository
) {
    operator fun invoke(): Flow<List<Note>> {
        return repository.getIndividualNotes()
    }
}