package com.example.teqnotes.features.auth.data.remote

import com.example.teqnotes.core.network.ApiEndpoints
import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import javax.inject.Inject

interface UserApi {
    suspend fun deleteAccount(): Result<Unit>
}

class UserApiImpl(private val httpClient: HttpClient) : UserApi {

    override suspend fun deleteAccount(): Result<Unit> = safeCall {
        httpClient.delete(ApiEndpoints.Users.DELETE_ME)
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