package com.example.teqnotes.features.notifications.di

import com.example.teqnotes.features.notifications.data.repository.NotificationRepositoryImpl
import com.example.teqnotes.features.notifications.domain.repository.NotificationRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class NotificationModule {

    @Binds
    abstract fun bindNotificationRepository(
        notificationRepositoryImpl: NotificationRepositoryImpl
    ): NotificationRepository
}