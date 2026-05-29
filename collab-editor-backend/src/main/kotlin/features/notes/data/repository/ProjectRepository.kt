package com.example.features.notes.data.repository

import com.example.features.notes.data.local.Project
import com.example.features.notes.domain.model.ProjectData
import com.example.features.notes.data.local.ProjectMember
import com.example.features.notes.domain.model.ProjectMemberData
import com.example.features.notes.data.local.ProjectMembers
import com.example.features.auth.data.model.User
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction

interface ProjectRepository {
    fun create(ownerId: Int, name: String, description: String?): ProjectData
    fun findById(id: Int): ProjectData?
    fun findAllByUser(userId: Int): List<Pair<ProjectData, ProjectMemberData>>
    fun update(id: Int, name: String?, description: String?)
    fun delete(id: Int)
    fun addMember(projectId: Int, userId: Int, role: String): ProjectMember
    fun getMember(projectId: Int, userId: Int): ProjectMemberData?
}

object ProjectRepositoryImpl : ProjectRepository {
    override fun create(ownerId: Int, name: String, description: String?): ProjectData = transaction {
        val project = Project.new {
            this.name = name
            this.description = description
            this.owner = User[ownerId]
        }

        ProjectMember.new {
            this.project = project
            this.user = User[ownerId]
            this.role = "OWNER"
        }

        ProjectData(
            id = project.id.value,
            name = project.name,
            description = project.description,
            ownerId = project.owner.id.value,
            createdAt = project.createdAt,
            updatedAt = project.updatedAt
        )
    }

    override fun findById(id: Int): ProjectData? = transaction {
        val project = Project.findById(id) ?: return@transaction null
        ProjectData(
            id = project.id.value,
            name = project.name,
            description = project.description,
            ownerId = project.owner.id.value,
            createdAt = project.createdAt,
            updatedAt = project.updatedAt
        )
    }

    override fun findAllByUser(userId: Int): List<Pair<ProjectData, ProjectMemberData>> = transaction {
        ProjectMember.find { ProjectMembers.userId eq userId }.map { member ->
            val proj = member.project // Загружаем проект
            val projData = ProjectData(
                id = proj.id.value,
                name = proj.name,
                description = proj.description,
                ownerId = proj.owner.id.value,
                createdAt = proj.createdAt,
                updatedAt = proj.updatedAt
            )
            val memberData = ProjectMemberData(
                projectId = member.project.id.value,
                userId = member.user.id.value,
                role = member.role
            )
            Pair(projData, memberData)
        }
    }

    override fun update(id: Int, name: String?, description: String?): Unit = transaction {
        val project = Project.findById(id) ?: throw IllegalArgumentException("Project not found")
        name?.let { project.name = it }
        description?.let { project.description = it }
    }

    override fun delete(id: Int): Unit = transaction {
        Project.findById(id)?.delete()
    }

    override fun addMember(projectId: Int, userId: Int, role: String) = transaction {
        ProjectMember.new {
            this.project = Project[projectId]
            this.user = User[userId]
            this.role = role
        }
    }

    override fun getMember(projectId: Int, userId: Int): ProjectMemberData? = transaction {
        val member = ProjectMember.find {
            (ProjectMembers.projectId eq projectId) and (ProjectMembers.userId eq userId)
        }.firstOrNull() ?: return@transaction null

        ProjectMemberData(
            projectId = member.project.id.value,
            userId = member.user.id.value,
            role = member.role
        )
    }
}