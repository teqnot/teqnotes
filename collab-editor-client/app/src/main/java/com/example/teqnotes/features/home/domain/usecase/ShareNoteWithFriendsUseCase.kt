package com.example.teqnotes.features.home.domain.usecase

import com.example.teqnotes.features.home.domain.repository.HomeRepository
import javax.inject.Inject

class ShareNoteWithFriendsUseCase @Inject constructor(
    private val repository: HomeRepository
) {
    suspend operator fun invoke(noteId: Int, friendIds: List<Int>, role: String): Result<Unit> =
        repository.shareNoteWithFriends(noteId, friendIds, role)
}