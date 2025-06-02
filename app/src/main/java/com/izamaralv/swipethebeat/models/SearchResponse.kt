package com.izamaralv.swipethebeat.models

import com.google.gson.annotations.SerializedName

// Respuesta de búsqueda
data class SearchResponse(
    @SerializedName("tracks") val tracks: TrackList?, // ✅ Nullable if searching artists
    @SerializedName("artists") val artists: ArtistList? // 🔥 Add support for artist search
)

data class ArtistList(
    @SerializedName("items") val items: List<Artist> // ✅ Artists list from search results
)

data class Artist(
    val id: String, // ID del artista
    val name: String, // Nombre del artista
    @SerializedName("images") val images: List<Image>? // ✅ Some artists have images, some don’t
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
    val previewUrl: String?, // URL de vista previa
    val id: String, // ID del track
    val uri: String // URI del track
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
