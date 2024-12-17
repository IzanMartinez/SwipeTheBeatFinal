package com.izamaralv.swipethebeat.models

import com.google.gson.annotations.SerializedName

// Datos de la respuesta de canciones favoritas
data class LikedSongsResponse(
    @SerializedName("items") val tracks: List<LikedSongItem> // Lista de canciones favoritas
)

// Datos de cada canción favorita
data class LikedSongItem(
    @SerializedName("track") val track: Track // Información de la canción
)
