package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dreams")
data class Dream(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val lucidity: Int = 1, // Scale 1 to 5
    val intensity: Int = 1, // Scale 1 to 5
    val moodTag: String = "Serene", // Serene, Anxious, Vivid, Mystical, Eerie
    val typeTag: String = "Mundane", // Lucid, Nightmare, Prophetic, Cosmic, Healing, Mundane
    val interpretation: String? = null,
    val archetypes: String? = null, // Comma separated list of archetypes
    val moonPhase: String = "Waxing Gibbous"
)
