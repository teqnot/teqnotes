package com.example.teqnotes.features.home.data.remote

import com.example.teqnotes.core.network.ApiEndpoints
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.utils.EmptyContent.contentType
import io.ktor.http.ContentType
import io.ktor.http.contentType
import javax.inject.Inject

interface ProjectApi {
    suspend fun getProjects(): Result<List<ProjectDto>>
    suspend fun createProject(request: CreateProjectRequest): Result<ProjectDto>
    suspend fun getProject(projectId: Int): Result<ProjectDto>
    suspend fun updateProject(projectId: Int, request: UpdateProjectRequest): Result<ProjectDto>
    suspend fun deleteProject(projectId: Int): Result<Unit>
}

class ProjectApiImpl @Inject constructor(
    private val httpClient: HttpClient
) : ProjectApi {

    override suspend fun getProjects(): Result<List<ProjectDto>> = safeCall {
        httpClient.get(ApiEndpoints.Projects.BASE).body()
    }

    override suspend fun createProject(request: CreateProjectRequest): Result<ProjectDto> = safeCall {
        httpClient.post(ApiEndpoints.Projects.BASE) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    override suspend fun getProject(projectId: Int): Result<ProjectDto> = safeCall {
        httpClient.get("${ApiEndpoints.Projects.BASE}/$projectId").body()
    }

    override suspend fun updateProject(projectId: Int, request: UpdateProjectRequest): Result<ProjectDto> = safeCall {
        httpClient.patch("${ApiEndpoints.Projects.BASE}/$projectId") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    override suspend fun deleteProject(projectId: Int): Result<Unit> = safeCall {
        httpClient.delete("${ApiEndpoints.Projects.BASE}/$projectId")
        Unit
    }

    private inline fun <reified T> safeCall(block: () -> T): Result<T> {
        return try {
            Result.success(block())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}