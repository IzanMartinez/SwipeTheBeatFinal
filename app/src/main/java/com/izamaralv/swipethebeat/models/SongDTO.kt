package com.izamaralv.swipethebeat.models

data class SongDTO(
    val id: String,
    val name: String,
    val artists: List<String>,
    val albumName: String,
    val albumCoverUrl: String,
    val durationMs: Int,
    val uri: String,
    val previewUrl: String
)

