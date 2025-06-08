package com.izamaralv.swipethebeat.models

import com.google.gson.annotations.SerializedName

// Resultado completo de una búsqueda en Spotify
data class SearchResponse(
    @SerializedName("tracks") val tracks: TrackList?,
    @SerializedName("artists") val artists: ArtistList?
)

// Lista de artistas devueltos por la búsqueda
data class ArtistList(
    @SerializedName("items") val items: List<Artist>
)

// Representa un artista
data class Artist(
    val id: String,
    val name: String,
    @SerializedName("images") val images: List<Image>?
)

// Lista de pistas devueltas por la búsqueda
data class TrackList(
    @SerializedName("items") val items: List<Track>
)

// Detalles de una canción
data class Track(
    val name: String,
    val artists: List<Artist>,
    val album: Album,
    val previewUrl: String?,
    val id: String,
    val uri: String
)

// Detalles de un álbum
data class Album(
    val id: String,
    val name: String,
    val images: List<Image>
)

// URL de una imagen asociada a álbum o artista
data class Image(
    val url: String
)
