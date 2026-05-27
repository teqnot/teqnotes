package com.example.teqnotes.features.home.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Query("""
        SELECT * FROM notes
        WHERE projectId IS NULL AND isArchived = 0
        ORDER BY timestamp DESC
    """)
    fun getIndividualNotes(): Flow<List<NoteEntity>>

    @Query("""
        SELECT * FROM notes
        WHERE projectId = :projectId AND isArchived = 0
        ORDER BY timestamp DESC
    """)
    fun getNotesByProject(projectId: String): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE isArchived = 0 ORDER BY timestamp DESC")
    fun getAllNotes(): Flow<List<NoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotes(notes: List<NoteEntity>)

    @Update
    suspend fun updateNote(note: NoteEntity)

    @Query("UPDATE notes SET isArchived = 1 WHERE id = :id")
    suspend fun archiveNote(id: String)
}