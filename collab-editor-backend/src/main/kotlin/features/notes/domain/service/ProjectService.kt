package com.example.features.notes.domain.service

import com.example.features.notes.presentation.dto.CreateProjectRequest
import com.example.features.notes.presentation.dto.ProjectResponse
import com.example.features.notes.presentation.dto.UpdateProjectRequest
import com.example.features.auth.data.repository.UserRepository
import com.example.features.notes.data.repository.ProjectRepository

class ProjectService(
    private val projectRepository: ProjectRepository,
    private val userRepository: UserRepository
) {

    fun createProject(ownerId: Int, request: CreateProjectRequest): Result<ProjectResponse> {
        return try {
            val project = projectRepository.create(ownerId, request.name, request.description)
            Result.success(
                ProjectResponse(
                    id = project.id,
                    name = project.name,
                    description = project.description,
                    ownerId = project.ownerId,
                    role = "OWNER"
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getUserProjects(userId: Int): Result<List<ProjectResponse>> {
        return try {
            val projects = projectRepository.findAllByUser(userId).map { (proj, member) ->
                ProjectResponse(
                    id = proj.id,
                    name = proj.name,
                    description = proj.description,
                    ownerId = proj.ownerId,
                    role = member.role
                )
            }
            Result.success(projects)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getProject(userId: Int, projectId: Int): Result<ProjectResponse> {
        return try {
            val project = projectRepository.findById(projectId) ?: throw IllegalArgumentException("Project not found")
            val member = projectRepository.getMember(projectId, userId) ?: throw IllegalArgumentException("Forbidden")

            Result.success(
                ProjectResponse(
                    id = project.id,
                    name = project.name,
                    description = project.description,
                    ownerId = project.ownerId,
                    role = member.role
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun updateProject(userId: Int, projectId: Int, request: UpdateProjectRequest): Result<ProjectResponse> {
        return try {
            val project = projectRepository.findById(projectId)
                ?: return Result.failure(IllegalArgumentException("Project not found"))

            val member = projectRepository.getMember(projectId, userId)
            if (member == null || member.role != "OWNER") {
                return Result.failure(IllegalArgumentException("Forbidden: Only owner can update project details"))
            }

            projectRepository.update(projectId, request.name, request.description)
            getProject(userId, projectId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun deleteProject(userId: Int, projectId: Int): Result<Unit> {
        return try {
            val project = projectRepository.findById(projectId)
                ?: return Result.failure(IllegalArgumentException("Project not found"))

            val member = projectRepository.getMember(projectId, userId)
            if (member == null || member.role != "OWNER") {
                return Result.failure(IllegalArgumentException("Forbidden: Only owner can delete project"))
            }

            projectRepository.delete(projectId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun addMemberToProject(adminUserId: Int, projectId: Int, newMemberEmail: String, role: String): Result<Unit> {
        return try {
            val adminMember = projectRepository.getMember(projectId, adminUserId)
            if (adminMember == null || adminMember.role != "OWNER") {
                return Result.failure(IllegalArgumentException("Forbidden: Only owner can add members"))
            }

            val newUser = userRepository.findByEmail(newMemberEmail)
                ?: throw IllegalArgumentException("User not found by email")

            if (projectRepository.getMember(projectId, newUser.id) != null) {
                return Result.failure(IllegalArgumentException("User is already a member"))
            }

            projectRepository.addMember(projectId, newUser.id, role)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}