package com.example.teqnotes.features.notifications.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {

    @Query("""
        SELECT * FROM notifications
        ORDER BY timestamp DESC
    """)
    fun getNotifications(): Flow<List<NotificationEntity>>

    @Query("""
        UPDATE notifications
        SET isRead = 1
        WHERE id = :id
    """)
    suspend fun markAsRead(id: String)

    @Query("""
        UPDATE notifications
        SET 
            isRead = CASE WHEN isBookmarked = 0 THEN 1 ELSE isRead END,
            isBookmarked = NOT isBookmarked
        WHERE id = :id
    """)
    suspend fun toggleBookmark(id: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotifications(
        notifications: List<NotificationEntity>
    )
}