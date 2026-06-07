package com.example.teqnotes.core.network

import java.net.URLEncoder

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
        const val LIST = BASE
        const val REQUESTS = "$BASE/requests"
        const val REQUEST = "$BASE/request"
        fun accept(requestId: Int) = "$BASE/accept/$requestId"
        fun reject(requestId: Int) = "$BASE/reject/$requestId"
    }

    object Users {
        const val BASE = "/users"
        fun search(query: String): String {
            val encoded = URLEncoder.encode(query, "UTF-8")
            return "$BASE/search?query=$encoded"
        }
        const val DELETE_ME = "$BASE/me"
    }
}