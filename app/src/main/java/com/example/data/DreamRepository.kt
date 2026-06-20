package com.example.data

import kotlinx.coroutines.flow.Flow

class DreamRepository(private val dreamDao: DreamDao) {
    val allDreams: Flow<List<Dream>> = dreamDao.getDreamsFlow()

    suspend fun insertDream(dream: Dream): Long {
        return dreamDao.insertDream(dream)
    }

    suspend fun deleteDream(dream: Dream) {
        dreamDao.deleteDream(dream)
    }

    suspend fun deleteDreamById(id: Int) {
        dreamDao.deleteDreamById(id)
    }

    suspend fun getDreamById(id: Int): Dream? {
        return dreamDao.getDreamById(id)
    }

    suspend fun clearAllDreams() {
        dreamDao.clearAllDreams()
    }
}
