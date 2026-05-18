package com.example.teqnotes.di

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.teqnotes.features.notifications.data.local.NotificationDao
import com.example.teqnotes.features.notifications.data.local.NotificationEntity

@Database(entities = [NotificationEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun notificationDao(): NotificationDao
}