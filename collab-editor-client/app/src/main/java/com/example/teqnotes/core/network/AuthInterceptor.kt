package com.example.teqnotes.core.network

import com.example.teqnotes.core.storage.TokenStorage
import io.ktor.client.plugins.api.createClientPlugin

object AuthInterceptor {

    operator fun invoke(tokenStorage: TokenStorage) = createClientPlugin("AuthInterceptor") {
        onRequest { request, _ ->
            val token = tokenStorage.getAccessToken()
            if (token != null) {
                request.headers.append("Authorization", "Bearer $token")
            }
        }
    }
}