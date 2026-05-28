package com.example.teqnotes.features.home.domain.usecase

import com.example.teqnotes.features.home.domain.repository.HomeRepository
import javax.inject.Inject

class DeleteNoteUseCase @Inject constructor(
    private val repository: HomeRepository
) {
    suspend operator fun invoke(noteId: String) {
        repository.deleteNote(noteId)
    }
}