package com.example.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Part(
    @Json(name = "text") val text: String? = null
)

@JsonClass(generateAdapter = true)
data class Content(
    @Json(name = "parts") val parts: List<Part>
)

@JsonClass(generateAdapter = true)
data class GenerateContentRequest(
    @Json(name = "contents") val contents: List<Content>,
    @Json(name = "generationConfig") val generationConfig: GenerationConfig? = null,
    @Json(name = "systemInstruction") val systemInstruction: Content? = null
)

@JsonClass(generateAdapter = true)
data class ResponseFormatText(
    @Json(name = "mimeType") val mimeType: String
)

@JsonClass(generateAdapter = true)
data class ResponseFormat(
    @Json(name = "text") val text: ResponseFormatText? = null
)

@JsonClass(generateAdapter = true)
data class GenerationConfig(
    @Json(name = "temperature") val temperature: Float? = null,
    @Json(name = "responseMimeType") val responseMimeType: String? = null
)

@JsonClass(generateAdapter = true)
data class GenerateContentResponse(
    @Json(name = "candidates") val candidates: List<Candidate>? = null
)

@JsonClass(generateAdapter = true)
data class Candidate(
    @Json(name = "content") val content: Content? = null
)

// The structure we want Gemini to return in JSON format
@JsonClass(generateAdapter = true)
data class DreamAnalysis(
    @Json(name = "title") val title: String,
    @Json(name = "interpretation") val interpretation: String,
    @Json(name = "archetypes") val archetypes: String, // Comma-separated
    @Json(name = "typeTag") val typeTag: String, // Lucid, Nightmare, Prophetic, Cosmic, Healing, Mundane
    @Json(name = "moodTag") val moodTag: String, // Serene, Anxious, Vivid, Mystical, Eerie
    @Json(name = "moonPhase") val moonPhase: String // e.g. Waxing Crescent
)

// The structure for Sandbox Oracle responses
@JsonClass(generateAdapter = true)
data class OracleResponse(
    @Json(name = "symbolTitle") val symbolTitle: String,
    @Json(name = "symbolMeaning") val symbolMeaning: String,
    @Json(name = "archetypalConnection") val archetypalConnection: String,
    @Json(name = "lucidTip") val lucidTip: String
)
