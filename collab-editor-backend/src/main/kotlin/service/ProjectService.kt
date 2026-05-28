package com.example.service

import com.example.dto.CreateProjectRequest
import com.example.dto.ProjectResponse
import com.example.dto.UpdateProjectRequest
import com.example.repository.ProjectRepository
import com.example.repository.UserRepository

class ProjectService(
    private val projectRepository: ProjectRepository,
    private val userRepository: UserRepository
) {

    fun createProject(ownerId: Int, request: CreateProjectRequest): Result<ProjectResponse> {
        return try {
            val project = projectRepository.create(ownerId, request.name, request.description)
            Result.success(ProjectResponse(
                id = project.id.value,
                name = project.name,
                description = project.description,
                ownerId = project.owner.id.value,
                role = "OWNER"
            ))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getUserProjects(userId: Int): Result<List<ProjectResponse>> {
        return try {
            val projects = projectRepository.findAllByUser(userId).map { (proj, member) ->
                ProjectResponse(
                    id = proj.id.value,
                    name = proj.name,
                    description = proj.description,
                    ownerId = proj.owner.id.value,
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

            Result.success(ProjectResponse(
                id = project.id.value,
                name = project.name,
                description = project.description,
                ownerId = project.owner.id.value,
                role = member.role
            ))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun updateProject(userId: Int, projectId: Int, request: UpdateProjectRequest): Result<ProjectResponse> {
        return try {
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

            if (projectRepository.getMember(projectId, newUser.id.value) != null) {
                return Result.failure(IllegalArgumentException("User is already a member"))
            }

            projectRepository.addMember(projectId, newUser.id.value, role)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}