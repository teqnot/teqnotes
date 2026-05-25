package com.example.teqnotes.features.home.domain.usecase

import com.example.teqnotes.features.home.domain.model.Note
import com.example.teqnotes.features.home.domain.repository.HomeRepository
import jakarta.inject.Inject

class CreateNoteUseCase @Inject constructor(
    private val repository: HomeRepository
) {
    suspend operator fun invoke(note: Note) {
        repository.createNote(note)
    }
}