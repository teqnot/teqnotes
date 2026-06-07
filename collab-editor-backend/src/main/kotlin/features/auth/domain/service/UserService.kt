package com.example.features.auth.domain.service

import com.example.features.auth.data.model.RefreshToken
import com.example.features.auth.data.model.RefreshTokens
import com.example.features.auth.data.model.User
import com.example.features.auth.data.repository.UserRepository
import com.example.features.friends.data.local.Friendship
import com.example.features.friends.data.local.Friendships
import com.example.features.notes.data.local.Note
import com.example.features.notes.data.local.NoteAccesses
import com.example.features.notes.data.local.Notes
import com.example.features.notes.data.local.Project
import com.example.features.notes.data.local.ProjectMembers
import com.example.features.notes.data.local.Projects
import com.example.shared.dto.UserSearchResult
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.transactions.transaction

class UserService(
    private val userRepository: UserRepository
) {
    fun searchUsers(currentUserId: Int, query: String): Result<List<UserSearchResult>> = try {
        val users = userRepository.searchUsers(query)
            .filter { it.id != currentUserId }
            .map { UserSearchResult(id = it.id, name = it.name, email = it.email) }
        Result.success(users)
    } catch (e: Exception) {
        Result.failure(e)
    }

    fun deleteAccount(userId: Int): Result<Unit> = try {
        transaction {
            NoteAccesses.deleteWhere { NoteAccesses.userId eq userId }
            Note.find { Notes.ownerId eq userId }.forEach { it.delete() }

            ProjectMembers.deleteWhere { ProjectMembers.userId eq userId }
            Project.find { Projects.ownerId eq userId }.forEach { it.delete() }

            Friendship.find {
                (Friendships.userId1 eq userId) or (Friendships.userId2 eq userId)
            }.forEach { it.delete() }

            RefreshToken.find { RefreshTokens.userId eq userId }.forEach { it.delete() }

            User.findById(userId)?.delete()
                ?: throw IllegalArgumentException("User not found")
        }
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}