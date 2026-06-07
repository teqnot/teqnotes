package com.example.teqnotes.features.friends.data.remote

import com.example.teqnotes.core.network.ApiEndpoints
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import javax.inject.Inject

interface FriendApi {
    suspend fun getFriends(): Result<List<FriendDto>>
    suspend fun getIncomingRequests(): Result<List<FriendshipResponseDto>>
    suspend fun searchUsers(query: String): Result<List<UserSearchResultDto>>
    suspend fun sendRequest(request: FriendRequestDto): Result<Unit>
    suspend fun acceptRequest(requestId: Int): Result<Unit>
    suspend fun rejectRequest(requestId: Int): Result<Unit>
}

class FriendApiImpl @Inject constructor(
    private val httpClient: HttpClient
) : FriendApi {

    override suspend fun getFriends(): Result<List<FriendDto>> = safeCall {
        httpClient.get(ApiEndpoints.Friends.LIST).body()
    }

    override suspend fun getIncomingRequests(): Result<List<FriendshipResponseDto>> = safeCall {
        httpClient.get(ApiEndpoints.Friends.REQUESTS).body()
    }

    override suspend fun searchUsers(query: String): Result<List<UserSearchResultDto>> = safeCall {
        httpClient.get(ApiEndpoints.Users.search(query)).body()
    }

    override suspend fun sendRequest(request: FriendRequestDto): Result<Unit> = safeCall {
        httpClient.post(ApiEndpoints.Friends.REQUEST) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        Unit
    }

    override suspend fun acceptRequest(requestId: Int): Result<Unit> = safeCall {
        httpClient.post(ApiEndpoints.Friends.accept(requestId))
        Unit
    }

    override suspend fun rejectRequest(requestId: Int): Result<Unit> = safeCall {
        httpClient.post(ApiEndpoints.Friends.reject(requestId))
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