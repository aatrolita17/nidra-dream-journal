package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DreamDao {
    @Query("SELECT * FROM dreams ORDER BY timestamp DESC")
    fun getDreamsFlow(): Flow<List<Dream>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDream(dream: Dream): Long

    @Delete
    suspend fun deleteDream(dream: Dream)

    @Query("DELETE FROM dreams WHERE id = :id")
    suspend fun deleteDreamById(id: Int)

    @Query("SELECT * FROM dreams WHERE id = :id LIMIT 1")
    suspend fun getDreamById(id: Int): Dream?

    @Query("DELETE FROM dreams")
    suspend fun clearAllDreams()
}
