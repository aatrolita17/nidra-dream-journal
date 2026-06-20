package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.data.Dream
import com.example.data.DreamDatabase
import com.example.data.DreamRepository
import com.example.network.Content
import com.example.network.DreamAnalysis
import com.example.network.GenerateContentRequest
import com.example.network.GenerationConfig
import com.example.network.OracleResponse
import com.example.network.Part
import com.example.network.RetrofitClient
import com.squareup.moshi.JsonAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed interface OracleUiState {
    object Idle : OracleUiState
    object Loading : OracleUiState
    data class Success(val response: OracleResponse) : OracleUiState
    data class Error(val message: String) : OracleUiState
}

sealed interface AddDreamUiState {
    object Idle : AddDreamUiState
    object Analyzing : AddDreamUiState
    object Success : AddDreamUiState
    data class Error(val message: String) : AddDreamUiState
}

class DreamViewModel(application: Application) : AndroidViewModel(application) {
    private val database = DreamDatabase.getDatabase(application)
    private val repository = DreamRepository(database.dreamDao())

    val allDreams: StateFlow<List<Dream>> = repository.allDreams
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _addDreamUiState = MutableStateFlow<AddDreamUiState>(AddDreamUiState.Idle)
    val addDreamUiState: StateFlow<AddDreamUiState> = _addDreamUiState.asStateFlow()

    private val _oracleUiState = MutableStateFlow<OracleUiState>(OracleUiState.Idle)
    val oracleUiState: StateFlow<OracleUiState> = _oracleUiState.asStateFlow()

    private val dreamAnalysisAdapter: JsonAdapter<DreamAnalysis> =
        RetrofitClient.moshi.adapter(DreamAnalysis::class.java)

    private val oracleResponseAdapter: JsonAdapter<OracleResponse> =
        RetrofitClient.moshi.adapter(OracleResponse::class.java)

    fun resetAddDreamState() {
        _addDreamUiState.value = AddDreamUiState.Idle
    }

    fun resetOracleState() {
        _oracleUiState.value = OracleUiState.Idle
    }

    /**
     * Analyzes a dream using Gemini API, parses the JSON result, and inserts it into the database.
     * Falls back to local smart heuristics if Gemini fails or if API key is empty.
     */
    fun analyzeAndSaveDream(
        content: String,
        manualLucidity: Int,
        manualIntensity: Int,
        manualMoodTag: String,
        manualTypeTag: String,
        moonPhase: String
    ) {
        viewModelScope.launch {
            _addDreamUiState.value = AddDreamUiState.Analyzing
            
            val apiKey = BuildConfig.GEMINI_API_KEY
            if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
                // API Key is not set - fallback to smart local heuristics
                saveFallbackDream(content, manualLucidity, manualIntensity, manualMoodTag, manualTypeTag, moonPhase, "API Key not configured in Secrets panel")
                return@launch
            }

            try {
                val systemPrompt = """
                    You are Nidra, the cosmic dream oracle. Analyze the dream description provided by the user. 
                    You must return EXACTLY a JSON block matching this schema: 
                    {
                      "title": "A short, beautiful, poetic, and mysterious 2-4 word dream title",
                      "interpretation": "A deep, symbolic, compassionate, and psychological/metaphorical interpretation of the dream. 2-3 sentences.",
                      "archetypes": "A comma-separated list of 2-3 prominent symbolic items or themes found (e.g. Water, Gold Key, Clock)",
                      "typeTag": "One of: Lucid, Nightmare, Prophetic, Cosmic, Healing, Mundane",
                      "moodTag": "One of: Serene, Anxious, Vivid, Mystical, Eerie",
                      "moonPhase": "A beautiful moon phase matching the vibe of this dream (e.g. Waning Gibbous, Full Moon, Blood Moon, Dark Void)"
                    }
                    Keep tags aligned to these strict strings. Return nothing but this JSON block.
                """.trimIndent()

                val requestBody = GenerateContentRequest(
                    contents = listOf(
                        Content(parts = listOf(Part(text = "Analyze this dream content:\n$content")))
                    ),
                    generationConfig = GenerationConfig(
                        temperature = 0.8f,
                        responseMimeType = "application/json"
                    ),
                    systemInstruction = Content(parts = listOf(Part(text = systemPrompt)))
                )

                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.service.generateContent(
                        model = "gemini-3.5-flash",
                        apiKey = apiKey,
                        request = requestBody
                    )
                }

                val jsonResponse = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                if (jsonResponse != null) {
                    val analysis = withContext(Dispatchers.Default) {
                        dreamAnalysisAdapter.fromJson(jsonResponse)
                    }
                    if (analysis != null) {
                        val finalDream = Dream(
                            title = analysis.title,
                            content = content,
                            lucidity = manualLucidity,
                            intensity = manualIntensity,
                            moodTag = analysis.moodTag,
                            typeTag = analysis.typeTag,
                            interpretation = analysis.interpretation,
                            archetypes = analysis.archetypes,
                            moonPhase = analysis.moonPhase
                        )
                        repository.insertDream(finalDream)
                        _addDreamUiState.value = AddDreamUiState.Success
                    } else {
                        throw Exception("Failed to serialize dream analysis")
                    }
                } else {
                    throw Exception("Oracle returned empty prophecy")
                }
            } catch (e: Exception) {
                // Network error or parsing error -> fallback safely
                saveFallbackDream(content, manualLucidity, manualIntensity, manualMoodTag, manualTypeTag, moonPhase, "Fallback Oracle: ${e.localizedMessage}")
            }
        }
    }

    private suspend fun saveFallbackDream(
         content: String,
         lucidity: Int,
         intensity: Int,
         moodTag: String,
         typeTag: String,
         moonPhase: String,
         reason: String
    ) {
        val calculatedTitle = if (content.length > 25) {
            content.take(22).trim() + "..."
        } else {
            "Nocturnal Echo"
        }

        // Local semantic heuristics to create a cool mood-based interpretation
        val automaticInterpretation = when {
            content.contains("falling", ignoreCase = true) -> 
                "Falling in dreams often suggests a subconscious feeling of vulnerability, a temporary loss of control in wakefulness, or an invitation to let go and trust the path ahead."
            content.contains("flying", ignoreCase = true) -> 
                "Flying symbolises a desire for liberation, rising above standard dualities, or experiencing a newly discovered state of spiritual sovereignty and clarity."
            content.contains("water", ignoreCase = true) || content.contains("ocean", ignoreCase = true) -> 
                "Water represents your emotional depths and subconscious currents. Calm pools mirror inner peace, while vast oceans call you to dive into unresolved feelings."
            content.contains("run", ignoreCase = true) || content.contains("chase", ignoreCase = true) -> 
                "Being chased outlines an aspect of yourself - a fear, a task, or a dynamic - that you are currently avoiding confronting in your daily life."
            else -> 
                "This nocturnal imprint reflects your deep subconscious synthesizing the events of your daily path. Meditate on the emotional tones felt during this journey to tap into its meaning."
        }

        // Local archetypes extraction
        val extractedArchetypesList = mutableListOf<String>()
        if (content.contains("water", ignoreCase = true) || content.contains("river", ignoreCase = true)) extractedArchetypesList.add("Water Source")
        if (content.contains("key", ignoreCase = true) || content.contains("lock", ignoreCase = true)) extractedArchetypesList.add("Hidden Portal")
        if (content.contains("shadow", ignoreCase = true) || content.contains("dark", ignoreCase = true)) extractedArchetypesList.add("The Shadow")
        if (content.contains("sky", ignoreCase = true) || content.contains("stars", ignoreCase = true)) extractedArchetypesList.add("Aether Sky")
        if (content.contains("door", ignoreCase = true) || content.contains("gate", ignoreCase = true)) extractedArchetypesList.add("Threshold")
        if (extractedArchetypesList.isEmpty()) {
            extractedArchetypesList.add("Astral Echo")
            extractedArchetypesList.add("Subconscious Glyph")
        }

        val fallbackDream = Dream(
            title = "Whisper of $calculatedTitle",
            content = content,
            lucidity = lucidity,
            intensity = intensity,
            moodTag = moodTag,
            typeTag = typeTag,
            interpretation = "$automaticInterpretation ($reason)",
            archetypes = extractedArchetypesList.joinToString(", "),
            moonPhase = moonPhase
        )

        repository.insertDream(fallbackDream)
        _addDreamUiState.value = AddDreamUiState.Success
    }

    /**
     * Queries the Subconscious Oracle about a custom symbol or question.
     */
    fun queryOracle(symbol: String) {
        viewModelScope.launch {
            _oracleUiState.value = OracleUiState.Loading

            val apiKey = BuildConfig.GEMINI_API_KEY
            if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
                // Return immediate local simulated oracle responses
                simulateOracleResponse(symbol, "Oracle key is offline.")
                return@launch
            }

            try {
                val systemPrompt = """
                    You are Nidra, the primordial subconscious interpreter and keeper of ancient mythos.
                    Decipher the User's symbol or query into a deep, aesthetic, and helpful symbolic breakdown.
                    You must return EXACTLY a JSON block following this schema:
                    {
                      "symbolTitle": "The Name of the Symbol or Concept analyzed",
                      "symbolMeaning": "A mystical, psychological, and poetic analysis of what this symbol represents in dreams. 2 sentences.",
                      "archetypalConnection": "The archetypal card or concept linked to this (e.g. The Hermit, The Gateway, Primordial Water, The Trickster). 1 sentence.",
                      "lucidTip": "A actionable tip on how to gain lucidity or navigate this symbol if encountered again in a dream. 1 sentence."
                    }
                    Provide beautiful, high-quality, high-vibe guidance. Return nothing but JSON.
                """.trimIndent()

                val requestBody = GenerateContentRequest(
                    contents = listOf(
                        Content(parts = listOf(Part(text = "Decipher this dream symbol/question: $symbol")))
                    ),
                    generationConfig = GenerationConfig(
                        temperature = 0.7f,
                        responseMimeType = "application/json"
                    ),
                    systemInstruction = Content(parts = listOf(Part(text = systemPrompt)))
                )

                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.service.generateContent(
                        model = "gemini-3.5-flash",
                        apiKey = apiKey,
                        request = requestBody
                    )
                }

                val jsonResponse = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                if (jsonResponse != null) {
                    val oracleResponse = withContext(Dispatchers.Default) {
                        oracleResponseAdapter.fromJson(jsonResponse)
                    }
                    if (oracleResponse != null) {
                        _oracleUiState.value = OracleUiState.Success(oracleResponse)
                    } else {
                        throw Exception("Oracle reading was blurry - could not parse")
                    }
                } else {
                    throw Exception("Oracle remained silent")
                }
            } catch (e: Exception) {
                simulateOracleResponse(symbol, "Fallback Cosmic Whisper: ${e.localizedMessage}")
            }
        }
    }

    private fun simulateOracleResponse(symbol: String, note: String) {
        val title = symbol.uppercase().trim()
        val meaning = when {
            symbol.contains("key", ignoreCase = true) -> 
                "Keys are indicators of access, containing the power to unlock secret potentials or close doors on past cycles. Your subconscious is signaling that you hold the answer."
            symbol.contains("cat", ignoreCase = true) || symbol.contains("kitten", ignoreCase = true) -> 
                "Feline spirits mirror intuition, hidden grace, independence, and the ability to walk gracefully in the dark. Your dreaming psyche urges you to trust your sensory vibes."
            symbol.contains("door", ignoreCase = true) -> 
                "A door reflects transition, thresholds between dimensions of consciousness, and choices of entry. An open door indicates readiness; a locked door represents fear of unknown aspects."
            symbol.contains("fly", ignoreCase = true) || symbol.contains("wings", ignoreCase = true) -> 
                "Flight highlights transcendence of heavy earthly challenges, spiritual elevation, and shifting into higher states of awareness. Trust your lightness."
            else -> 
                "This symbol represents a unique subconscious projection of your deep current life path. It serves as an internal anchor or mirror to your inner psychological transition."
        }

        val archetype = when {
            symbol.contains("key", ignoreCase = true) -> "The Guardian of Keys"
            symbol.contains("cat", ignoreCase = true) -> "The Mystic Familiar"
            symbol.contains("door", ignoreCase = true) -> "The Astral Gatekeeper"
            symbol.contains("fly", ignoreCase = true) -> "The Boundless Sky Traveler"
            else -> "The Nocturnal Wanderer"
        }

        val tips = "To gain lucidity when encountering $symbol, look closely at your hands and ask out loud, 'Is this a dream?'. Let its occurrence be your trigger."

        _oracleUiState.value = OracleUiState.Success(
            OracleResponse(
                symbolTitle = title,
                symbolMeaning = "$meaning ($note)",
                archetypalConnection = archetype,
                lucidTip = tips
            )
        )
    }

    fun deleteDream(dream: Dream) {
        viewModelScope.launch {
            repository.deleteDream(dream)
        }
    }

    fun deleteDreamById(id: Int) {
        viewModelScope.launch {
            repository.deleteDreamById(id)
        }
    }

    fun clearAllDreams() {
        viewModelScope.launch {
            repository.clearAllDreams()
        }
    }
}
