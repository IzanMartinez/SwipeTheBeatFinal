package com.izamaralv.swipethebeat.models

import com.google.gson.annotations.SerializedName

// Datos de la respuesta de canciones favoritas
data class LikedSongsResponse(
    @SerializedName("items") val tracks: List<LikedSongItem>
)

// Datos de cada canción favorita
data class LikedSongItem(
    @SerializedName("track") val track: Track
)
