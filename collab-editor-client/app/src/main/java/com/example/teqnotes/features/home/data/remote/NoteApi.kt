package com.example.teqnotes.features.home.data.remote

import com.example.teqnotes.core.network.ApiEndpoints
import com.example.teqnotes.features.sharing.data.remote.AddMemberRequest
import com.example.teqnotes.features.sharing.data.remote.ShareNoteRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import javax.inject.Inject

interface NoteApi {
    suspend fun getAllNotes(): Result<List<NoteDto>>
    suspend fun createNote(request: CreateNoteRequest): Result<NoteDto>
    suspend fun getNote(noteId: Int): Result<NoteDto>
    suspend fun updateNote(noteId: Int, request: UpdateNoteRequest): Result<NoteDto>
    suspend fun deleteNote(noteId: Int): Result<Unit>
    suspend fun shareNote(noteId: Int, request: ShareNoteRequest): Result<Unit>
}

class NoteApiImpl @Inject constructor(
    private val httpClient: HttpClient
) : NoteApi {

    override suspend fun getAllNotes(): Result<List<NoteDto>> = safeCall {
        httpClient.get(ApiEndpoints.Notes.BASE).body()
    }

    override suspend fun createNote(request: CreateNoteRequest): Result<NoteDto> = safeCall {
        httpClient.post(ApiEndpoints.Notes.BASE) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    override suspend fun getNote(noteId: Int): Result<NoteDto> = safeCall {
        httpClient.get("${ApiEndpoints.Notes.BASE}/$noteId").body()
    }

    override suspend fun updateNote(noteId: Int, request: UpdateNoteRequest): Result<NoteDto> = safeCall {
        httpClient.patch("${ApiEndpoints.Notes.BASE}/$noteId") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    override suspend fun deleteNote(noteId: Int): Result<Unit> = safeCall {
        httpClient.delete("${ApiEndpoints.Notes.BASE}/$noteId")
        Unit
    }

    override suspend fun shareNote(noteId: Int, request: ShareNoteRequest): Result<Unit> = safeCall {
        httpClient.post(ApiEndpoints.Notes.share(noteId)) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
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