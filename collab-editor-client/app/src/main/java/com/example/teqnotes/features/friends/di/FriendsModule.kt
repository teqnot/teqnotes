package com.example.teqnotes.features.friends.di

import com.example.teqnotes.features.friends.data.repository.FriendRepositoryImpl
import com.example.teqnotes.features.friends.domain.repository.FriendRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class FriendsModule {

    @Binds
    abstract fun bindFriendRepository(
        friendRepositoryImpl: FriendRepositoryImpl
    ): FriendRepository
}