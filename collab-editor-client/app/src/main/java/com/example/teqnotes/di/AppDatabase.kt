package com.example.teqnotes.di

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.teqnotes.features.friends.data.local.FriendEntity
import com.example.teqnotes.features.friends.data.local.FriendDao
import com.example.teqnotes.features.home.data.local.NoteDao
import com.example.teqnotes.features.home.data.local.NoteEntity
import com.example.teqnotes.features.home.data.local.ProjectDao
import com.example.teqnotes.features.home.data.local.ProjectEntity
import com.example.teqnotes.features.notifications.data.local.NotificationDao
import com.example.teqnotes.features.notifications.data.local.NotificationEntity

@Database(
    entities = [
        NotificationEntity::class,
        NoteEntity::class,
        ProjectEntity::class,
        FriendEntity::class
    ],
    version = 3, // ver up
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun notificationDao(): NotificationDao
    abstract fun noteDao(): NoteDao
    abstract fun projectDao(): ProjectDao
    abstract fun friendDao(): FriendDao
}