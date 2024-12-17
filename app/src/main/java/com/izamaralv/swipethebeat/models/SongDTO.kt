package com.izamaralv.swipethebeat.models

// Datos de la canción
data class SongDTO(
    val id: String, // ID de la canción
    val name: String, // Nombre de la canción
    val artists: List<String>, // Lista de artistas
    val albumName: String, // Nombre del álbum
    val albumCoverUrl: String, // URL de la portada del álbum
    val durationMs: Int, // Duración en milisegundos
    val uri: String, // URI de la canción
    val previewUrl: String // URL de vista previa
)

