package com.example.teqnotes.core.network

object ApiEndpoints {
    private const val BASE_PATH = "/api"

    object Auth {
        const val REGISTER = "$BASE_PATH/auth/register"
        const val LOGIN = "$BASE_PATH/auth/login"
        const val REFRESH = "$BASE_PATH/auth/refresh"
    }

    object Notes {
        const val BASE = "$BASE_PATH/notes"
        fun byId(id: Int) = "$BASE/$id"
        fun share(id: Int) = "$BASE/$id/share"
    }

    object Projects {
        const val BASE = "$BASE_PATH/projects"
        fun byId(id: Int) = "$BASE/$id"
        fun members(id: Int) = "$BASE/$id/members"
    }

    object Friends {
        const val BASE = "$BASE_PATH/friends"
        const val REQUEST = "$BASE/request"
        const val REQUESTS = "$BASE/requests"
        fun accept(id: Int) = "$BASE/accept/$id"
        fun reject(id: Int) = "$BASE/reject/$id"
    }
}