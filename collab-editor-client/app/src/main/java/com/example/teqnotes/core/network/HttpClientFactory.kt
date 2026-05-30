package com.example.teqnotes.core.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.api.ClientPlugin
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.logging.Logger
import kotlinx.serialization.json.Json

object HttpClientFactory {

    fun create(
        baseUrl: String,
        authPlugin: ClientPlugin<*>? = null
    ): HttpClient {
        return HttpClient(Android) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                    explicitNulls = false
                })
            }

            install(io.ktor.client.plugins.logging.Logging) {
                logger = io.ktor.client.plugins.logging.Logger.DEFAULT
                level = io.ktor.client.plugins.logging.LogLevel.BODY
            }

            defaultRequest {
                url(baseUrl)
                contentType(ContentType.Application.Json)
            }

            authPlugin?.let { install(it) }
        }
    }
}