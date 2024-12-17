package com.izamaralv.swipethebeat.models

import com.google.gson.annotations.SerializedName

// Respuesta de búsqueda
data class SearchResponse(
    @SerializedName("tracks") val tracks: TrackList // Lista de tracks encontrados
)

// Lista de tracks
data class TrackList(
    @SerializedName("items") val items: List<Track> // Items de tracks
)

// Información de un track
data class Track(
    val name: String, // Nombre del track
    val artists: List<Artist>, // Lista de artistas
    val album: Album, // Información del álbum
    val preview_url: String?, // URL de vista previa
    val id: String, // ID del track
    val uri: String // URI del track
)

// Información de un artista
data class Artist(
    val id: String, // ID del artista
    val name: String // Nombre del artista
)

// Información de un álbum
data class Album(
    val id: String, // ID del álbum
    val name: String, // Nombre del álbum
    val images: List<Image> // Lista de imágenes del álbum
)

// Información de una imagen
data class Image(
    val url: String // URL de la imagen
)
