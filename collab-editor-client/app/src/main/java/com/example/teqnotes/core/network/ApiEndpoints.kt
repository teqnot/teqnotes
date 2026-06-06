package com.example.teqnotes.core.network

object ApiEndpoints {
    object Auth {
        const val REGISTER = "/auth/register"
        const val LOGIN = "/auth/login"
        const val REFRESH = "/auth/refresh"
    }

    object Notes {
        const val BASE = "/notes"
        fun byId(id: Int) = "$BASE/$id"
        fun share(id: Int) = "$BASE/$id/share"
    }

    object Projects {
        const val BASE = "/projects"
        fun byId(id: Int) = "$BASE/$id"
        fun members(id: Int) = "$BASE/$id/members"
    }

    object Friends {
        const val BASE = "/friends"
        const val REQUEST = "$BASE/request"
        const val REQUESTS = "$BASE/requests"
        fun accept(id: Int) = "$BASE/accept/$id"
        fun reject(id: Int) = "$BASE/reject/$id"
    }
}