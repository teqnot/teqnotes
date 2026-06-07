package com.example.teqnotes.di

import com.example.teqnotes.BuildConfig
import com.example.teqnotes.core.network.AuthInterceptor
import com.example.teqnotes.core.network.HttpClientFactory
import com.example.teqnotes.core.storage.TokenStorage
import com.example.teqnotes.features.auth.data.remote.AuthApi
import com.example.teqnotes.features.auth.data.remote.UserApi
import com.example.teqnotes.features.auth.data.remote.UserApiImpl
import com.example.teqnotes.features.friends.data.remote.FriendApi
import com.example.teqnotes.features.friends.data.remote.FriendApiImpl
import com.example.teqnotes.features.home.data.remote.NoteApi
import com.example.teqnotes.features.home.data.remote.NoteApiImpl
import com.example.teqnotes.features.home.data.remote.ProjectApi
import com.example.teqnotes.features.home.data.remote.ProjectApiImpl
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

    @Provides
    fun provideUserApi(httpClient: HttpClient): UserApi = UserApiImpl(httpClient)

    @Provides
    fun provideNoteApi(httpClient: HttpClient): NoteApi = NoteApiImpl(httpClient)

    @Provides
    fun provideProjectApi(httpClient: HttpClient): ProjectApi = ProjectApiImpl(httpClient)

    @Provides
    fun provideFriendApi(httpClient: HttpClient): FriendApi = FriendApiImpl(httpClient)
}