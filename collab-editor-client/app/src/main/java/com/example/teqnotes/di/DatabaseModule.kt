package com.example.teqnotes.di

import android.content.Context
import androidx.room.Room
import com.example.teqnotes.features.friends.data.local.FriendDao
import com.example.teqnotes.features.home.data.local.NoteDao
import com.example.teqnotes.features.home.data.local.ProjectDao
import com.example.teqnotes.features.notifications.data.local.NotificationDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "teq_notes_database"
        )
            .build()
    }

    @Provides
    @Singleton
    fun provideNotificationDao(database: AppDatabase): NotificationDao {
        return database.notificationDao()
    }

    @Provides
    @Singleton
    fun provideNoteDao(database: AppDatabase): NoteDao {
        return database.noteDao()
    }

    @Provides
    @Singleton
    fun provideProjectDao(database: AppDatabase): ProjectDao {
        return database.projectDao()
    }

    @Provides
    @Singleton
    fun provideFriendDao(database: AppDatabase): FriendDao {
        return database.friendDao()
    }
}