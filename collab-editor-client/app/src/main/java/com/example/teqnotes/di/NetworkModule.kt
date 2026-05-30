package com.example.teqnotes.di

import com.example.teqnotes.BuildConfig
import com.example.teqnotes.core.network.AuthInterceptor
import com.example.teqnotes.core.network.HttpClientFactory
import com.example.teqnotes.core.storage.TokenStorage
import com.example.teqnotes.features.auth.data.remote.AuthApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideHttpClient(tokenStorage: TokenStorage): HttpClient {
        return HttpClientFactory.create(
            baseUrl = BuildConfig.BASE_URL,
            authPlugin = AuthInterceptor(tokenStorage)
        )
    }

    @Provides
    fun provideAuthApi(httpClient: HttpClient): AuthApi {
        return AuthApi(httpClient)
    }
}