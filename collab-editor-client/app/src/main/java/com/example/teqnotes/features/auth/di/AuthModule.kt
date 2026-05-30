package com.example.teqnotes.features.auth.di

import com.example.teqnotes.core.storage.TokenStorage
import com.example.teqnotes.features.auth.data.remote.AuthApi
import com.example.teqnotes.features.auth.data.repository.AuthRepositoryImpl
import com.example.teqnotes.features.auth.domain.repository.AuthRepository
import com.example.teqnotes.features.auth.domain.usecase.LoginUseCase
import com.example.teqnotes.features.auth.domain.usecase.LogoutUseCase
import com.example.teqnotes.features.auth.domain.usecase.RegisterUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Provides
    @Singleton
    fun provideAuthRepository(api: AuthApi, storage: TokenStorage): AuthRepository {
        return AuthRepositoryImpl(api, storage)
    }

    @Provides
    fun provideLoginUseCase(repository: AuthRepository): LoginUseCase {
        return LoginUseCase(repository)
    }

    @Provides
    fun provideRegisterUseCase(repository: AuthRepository): RegisterUseCase {
        return RegisterUseCase(repository)
    }

    @Provides
    fun provideLogoutUseCase(repository: AuthRepository): LogoutUseCase {
        return LogoutUseCase(repository)
    }
}