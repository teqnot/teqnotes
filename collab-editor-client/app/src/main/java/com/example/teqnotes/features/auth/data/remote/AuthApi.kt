package com.example.teqnotes.features.auth.data.remote

import com.example.teqnotes.core.network.ApiEndpoints
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.isSuccess

class AuthApi(private val httpClient: HttpClient) {

    suspend fun register(request: RegisterRequest): Result<AuthResponse> {
        return try {
            val response = httpClient.post(ApiEndpoints.Auth.REGISTER) {
                setBody(request)
            }
            if (response.status.isSuccess()) {
                Result.success(response.body())
            } else {
                val error = runCatching { response.body<ErrorResponse>() }.getOrNull()
                Result.failure(Exception(error?.error ?: "Registration failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(request: LoginRequest): Result<AuthResponse> {
        return try {
            val response = httpClient.post(ApiEndpoints.Auth.LOGIN) {
                setBody(request)
            }
            if (response.status.isSuccess()) {
                Result.success(response.body())
            } else {
                val error = runCatching { response.body<ErrorResponse>() }.getOrNull()
                Result.failure(Exception(error?.error ?: "Login failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun refresh(request: RefreshRequest): Result<AuthResponse> {
        return try {
            val response = httpClient.post(ApiEndpoints.Auth.REFRESH) {
                setBody(request)
            }
            if (response.status.isSuccess()) {
                Result.success(response.body())
            } else {
                Result.failure(Exception("Refresh failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}